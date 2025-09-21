package com.zerox80.riotapi.service;

import com.zerox80.riotapi.model.LeagueEntryDTO;
import com.zerox80.riotapi.model.MatchV5Dto;
import com.zerox80.riotapi.model.PlayerLpRecord;
import com.zerox80.riotapi.model.Summoner;
import com.zerox80.riotapi.repository.PlayerLpRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class PlayerLpRecordService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerLpRecordService.class);
    private final PlayerLpRecordRepository playerLpRecordRepository;

    @Autowired
    public PlayerLpRecordService(PlayerLpRecordRepository playerLpRecordRepository) {
        this.playerLpRecordRepository = playerLpRecordRepository;
    }

    @Transactional
    public void savePlayerLpRecords(String puuid, List<LeagueEntryDTO> leagueEntries, Instant timestamp) {
        Instant ts = timestamp != null ? timestamp : Instant.now();
        for (LeagueEntryDTO entry : leagueEntries) {
            if ("RANKED_SOLO_5x5".equals(entry.getQueueType()) || "RANKED_FLEX_SR".equals(entry.getQueueType())) {
                PlayerLpRecord record = new PlayerLpRecord(
                        puuid,
                        entry.getQueueType(),
                        ts,
                        entry.getLeaguePoints(),
                        entry.getTier(),
                        entry.getRank()
                );
                playerLpRecordRepository.save(record);
                logger.debug("Saved LP record for puuid {}, queue {}: {} LP at {}", maskPuuid(puuid), entry.getQueueType(), entry.getLeaguePoints(), ts);
            }
        }
    }

    public void calculateAndSetLpChangesForMatches(Summoner summoner, List<MatchV5Dto> matchHistory) {
        if (summoner == null || !StringUtils.hasText(summoner.getPuuid()) || matchHistory == null || matchHistory.isEmpty()) {
            return;
        }

        Map<String, List<PlayerLpRecord>> recordsByQueue = preloadRankedRecords(summoner.getPuuid(), matchHistory);

        for (MatchV5Dto match : matchHistory) {
            if (match == null || match.getInfo() == null) {
                continue;
            }

            String queueTypeForDbQuery = resolveRankedQueueType(match.getInfo().getQueueId());
            if (queueTypeForDbQuery == null) {
                continue;
            }

            long endMillis = match.getInfo().getGameEndTimestamp();
            if (endMillis <= 0L) {
                logger.debug("Skipping LP change for match {} due to missing/invalid gameEndTimestamp.", safeMatchId(match));
                continue;
            }
            Instant matchEndTime = Instant.ofEpochMilli(endMillis);

            try {
                List<PlayerLpRecord> records = recordsByQueue.getOrDefault(queueTypeForDbQuery, Collections.emptyList());
                if (records.isEmpty()) {
                    logger.debug("No LP history available for puuid {} queue {}.", maskPuuid(summoner.getPuuid()), queueTypeForDbQuery);
                    continue;
                }

                PlayerLpRecord recordBefore = findRecordBefore(records, matchEndTime);
                PlayerLpRecord recordAfter = findRecordAfter(records, matchEndTime);

                if (recordBefore == null || recordAfter == null) {
                    logger.debug("LP records before or after match {} not found for PUUID {} and queue {}. Cannot calculate LP change.",
                            safeMatchId(match), maskPuuid(summoner.getPuuid()), queueTypeForDbQuery);
                    continue;
                }

                if (recordAfter.getTimestamp().isBefore(matchEndTime)) {
                    logger.debug("LP record after match {} for PUUID {} (queue {}) occurs before match end time {}.",
                            safeMatchId(match), maskPuuid(summoner.getPuuid()), queueTypeForDbQuery, matchEndTime);
                    continue;
                }

                int lpBefore = recordBefore.getLeaguePoints();
                int lpAfter = recordAfter.getLeaguePoints();
                int lpChange = lpAfter - lpBefore;

                if (!Objects.equals(recordBefore.getTier(), recordAfter.getTier()) || !Objects.equals(recordBefore.getRank(), recordAfter.getRank())) {
                    logger.warn("Tier/Rank changed for match {}. PUUID: {}. Before: {} {} {} LP, After: {} {} {} LP. LP Change calculation might be inaccurate or represent promotion/demotion.",
                            safeMatchId(match), maskPuuid(summoner.getPuuid()),
                            recordBefore.getTier(), recordBefore.getRank(), recordBefore.getLeaguePoints(),
                            recordAfter.getTier(), recordAfter.getRank(), recordAfter.getLeaguePoints());
                    match.getInfo().setLpChange(null);
                } else {
                    match.getInfo().setLpChange(lpChange);
                }
            } catch (Exception e) {
                logger.error("Error calculating LP change for match {} PUUID {}: {}", safeMatchId(match), maskPuuid(summoner.getPuuid()), e.getMessage(), e);
            }
        }
    }

    private Map<String, List<PlayerLpRecord>> preloadRankedRecords(String puuid, List<MatchV5Dto> matchHistory) {
        Set<String> queues = new HashSet<>();
        for (MatchV5Dto match : matchHistory) {
            if (match == null || match.getInfo() == null) {
                continue;
            }
            String queue = resolveRankedQueueType(match.getInfo().getQueueId());
            if (queue != null) {
                queues.add(queue);
            }
        }

        Map<String, List<PlayerLpRecord>> cache = new HashMap<>();
        for (String queue : queues) {
            try {
                List<PlayerLpRecord> recordsDesc = playerLpRecordRepository.findByPuuidAndQueueTypeOrderByTimestampDesc(puuid, queue);
                if (recordsDesc.isEmpty()) {
                    cache.put(queue, Collections.emptyList());
                } else {
                    List<PlayerLpRecord> ascending = new ArrayList<>(recordsDesc);
                    Collections.reverse(ascending);
                    cache.put(queue, ascending);
                }
            } catch (Exception e) {
                logger.error("Failed to preload LP records for puuid {} queue {}: {}", maskPuuid(puuid), queue, e.getMessage(), e);
                cache.put(queue, Collections.emptyList());
            }
        }
        return cache;
    }

    private String resolveRankedQueueType(int queueId) {
        return switch (queueId) {
            case 420 -> "RANKED_SOLO_5x5";
            case 440 -> "RANKED_FLEX_SR";
            default -> null;
        };
    }

    private PlayerLpRecord findRecordBefore(List<PlayerLpRecord> records, Instant timestamp) {
        int low = 0;
        int high = records.size() - 1;
        PlayerLpRecord candidate = null;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            PlayerLpRecord current = records.get(mid);
            if (current.getTimestamp().isBefore(timestamp)) {
                candidate = current;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return candidate;
    }

    private PlayerLpRecord findRecordAfter(List<PlayerLpRecord> records, Instant timestamp) {
        int low = 0;
        int high = records.size() - 1;
        PlayerLpRecord candidate = null;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            PlayerLpRecord current = records.get(mid);
            if (current.getTimestamp().isBefore(timestamp)) {
                low = mid + 1;
            } else {
                candidate = current;
                high = mid - 1;
            }
        }
        return candidate;
    }

    private static String maskPuuid(String puuid) {
        if (puuid == null) return "(null)";
        int len = puuid.length();
        if (len <= 10) return "***";
        return puuid.substring(0, 6) + "..." + puuid.substring(len - 4);
    }

    private static String safeMatchId(MatchV5Dto match) {
        try {
            if (match != null && match.getMetadata() != null && match.getMetadata().getMatchId() != null) {
                return match.getMetadata().getMatchId();
            }
        } catch (Exception ignored) {}
        return "(unknown)";
    }
}
