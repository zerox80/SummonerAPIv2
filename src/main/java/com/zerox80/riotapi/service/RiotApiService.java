package com.zerox80.riotapi.service;

import com.zerox80.riotapi.client.RiotApiClient;
import com.zerox80.riotapi.model.*;
import com.zerox80.riotapi.service.PlayerLpRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.Optional;
import com.zerox80.riotapi.util.ListUtils;

@Service
public class RiotApiService {

    private static final Logger logger = LoggerFactory.getLogger(RiotApiService.class);
    private final RiotApiClient riotApiClient;
    private final PlayerLpRecordService playerLpRecordService;

    @Autowired
    public RiotApiService(RiotApiClient riotApiClient,
                          PlayerLpRecordService playerLpRecordService) {
        this.riotApiClient = riotApiClient;
        this.playerLpRecordService = playerLpRecordService;
    }

    /**
     * Paged variant: fetch match history with offset (start) and count.
     * Uses Riot API's start/count parameters for the ID list and then hydrates details in small batches.
     */
    public CompletableFuture<List<MatchV5Dto>> getMatchHistoryPaged(String puuid, int start, int count) {
        if (!StringUtils.hasText(puuid)) {
            logger.error("Error: PUUID cannot be empty when fetching match history.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        if (count <= 0) {
            logger.error("Error: Count must be positive.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        if (start < 0) start = 0;

        final int from = start;
        final int limit = count;
        logger.info("Fetching paged match IDs for PUUID: {}, start={}, count={}...", maskPuuid(puuid), from, limit);
        return riotApiClient.getMatchIdsByPuuid(puuid, from, limit)
                .thenCompose(matchIds -> {
                    if (matchIds == null || matchIds.isEmpty()) {
                        return CompletableFuture.completedFuture(Collections.<MatchV5Dto>emptyList());
                    }
                    List<List<String>> batches = ListUtils.partition(matchIds, 5);
                    List<CompletableFuture<List<MatchV5Dto>>> batchFutures = batches.stream()
                            .map(this::fetchMatchBatch)
                            .collect(Collectors.toList());
                    CompletableFuture<Void> allDone = CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0]));
                    return allDone.thenApply(v -> batchFutures.stream()
                            .flatMap(f -> {
                                List<MatchV5Dto> list = f.join();
                                return list != null ? list.stream() : Stream.<MatchV5Dto>empty();
                            })
                            .filter(java.util.Objects::nonNull)
                            .collect(Collectors.toList()));
                })
                .exceptionally(ex -> {
                    logger.error("Error fetching paged match history for puuid {}: {}", maskPuuid(puuid), ex.getMessage(), ex);
                    return Collections.<MatchV5Dto>emptyList();
                });
    }

    public CompletableFuture<Summoner> getSummonerByRiotId(String gameName, String tagLine) {
        if (!StringUtils.hasText(gameName) || !StringUtils.hasText(tagLine)) {
            logger.error("Error: Game name and tag line cannot be empty.");
            return CompletableFuture.completedFuture(null);
        }

        logger.info("Searching for account: {}#{}...", gameName, tagLine);
        return riotApiClient.getAccountByRiotId(gameName, tagLine)
                .thenCompose(account -> {
                    if (account != null && StringUtils.hasText(account.getPuuid())) {
                        logger.info("Account found, PUUID: {}. Fetching summoner data...", maskPuuid(account.getPuuid()));
                        return riotApiClient.getSummonerByPuuid(account.getPuuid())
                                .thenApply(summoner -> {
                                    if (summoner != null) {
                                        if (StringUtils.hasText(account.getGameName())) {
                                            summoner.setName(account.getGameName());
                                        } else {
                                            summoner.setName(gameName);
                                            logger.warn("Warning: gameName is missing from AccountDto for PUUID: {}. Using provided gameName for Summoner object.", maskPuuid(account.getPuuid()));
                                        }
                                    }
                                    return summoner;
                                });
                    } else {
                        logger.info("Account for {}#{} not found or PUUID is missing.", gameName, tagLine);
                        return CompletableFuture.completedFuture(null);
                    }
                }).exceptionally(ex -> {
                    logger.error("Error fetching summoner data for {}#{}: {}", gameName, tagLine, ex.getMessage(), ex);
                    return null;
                });
    }

    public CompletableFuture<List<LeagueEntryDTO>> getLeagueEntries(String puuid) {
        if (!StringUtils.hasText(puuid)) {
            logger.error("Error: PUUID cannot be empty.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        logger.info("Fetching league entries (by PUUID) for PUUID: {}...", maskPuuid(puuid));

        return riotApiClient.getLeagueEntriesByPuuid(puuid)
                .thenApply(leagueEntries -> {
                    if (leagueEntries != null && !leagueEntries.isEmpty()) {
                        Instant now = Instant.now();
                        playerLpRecordService.savePlayerLpRecords(puuid, leagueEntries, now);
                    }
                    return leagueEntries;
                })
                .exceptionally(ex -> {
                    logger.error("Error fetching or processing league entries for puuid {}: {}",
                            maskPuuid(puuid), ex.getMessage(), ex);
                    return Collections.emptyList();
                });
    }

    @Cacheable(value = "matchHistory", key = "#puuid + '-' + #numberOfMatches")
    public CompletableFuture<List<MatchV5Dto>> getMatchHistory(String puuid, int numberOfMatches) {
        if (!StringUtils.hasText(puuid)) {
            logger.error("Error: PUUID cannot be empty when fetching match history.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        if (numberOfMatches <= 0) {
            logger.error("Error: Number of matches to fetch must be positive.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        logger.info("Fetching last {} match IDs for PUUID: {}...", numberOfMatches, maskPuuid(puuid));
        return riotApiClient.getMatchIdsByPuuid(puuid, numberOfMatches)
                .thenCompose(matchIds -> {
                    if (matchIds.isEmpty()) {
                        logger.info("No match IDs found for PUUID: {}", maskPuuid(puuid));
                        return CompletableFuture.completedFuture(Collections.<MatchV5Dto>emptyList());
                    }
                    logger.info("Fetching details for {} matches in batches...", matchIds.size());

                    List<List<String>> batches = ListUtils.partition(matchIds, 5);
                    List<CompletableFuture<List<MatchV5Dto>>> batchFutures = batches.stream()
                            .map(this::fetchMatchBatch)
                            .collect(Collectors.toList());

                    CompletableFuture<Void> allDone = CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0]));
                    return allDone.thenApply(v -> batchFutures.stream()
                            .flatMap(f -> {
                                List<MatchV5Dto> list = f.join();
                                return list != null ? list.stream() : Stream.<MatchV5Dto>empty();
                            })
                            .filter(java.util.Objects::nonNull)
                            .collect(Collectors.toList()));
                }).exceptionally(ex -> {
                    logger.error("Error fetching match history for puuid {}: {}", maskPuuid(puuid), ex.getMessage(), ex);
                    return Collections.emptyList();
                });
    }

    private CompletableFuture<List<MatchV5Dto>> fetchMatchBatch(List<String> matchIdBatch) {
        List<CompletableFuture<MatchV5Dto>> matchDetailFutures = matchIdBatch.stream()
            .map(matchId -> riotApiClient.getMatchDetails(matchId)
                .exceptionally(ex -> {
                    logger.error("Error fetching details for match ID {}: {}", matchId, ex.getMessage(), ex);
                    return null;
                }))
            .collect(Collectors.toList());

        return CompletableFuture.allOf(matchDetailFutures.toArray(new CompletableFuture[0]))
            .thenApply(v -> matchDetailFutures.stream()
                .map(CompletableFuture::join)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public Map<String, Long> getChampionPlayCounts(List<MatchV5Dto> matches, String searchedPuuid) {
        if (matches == null || matches.isEmpty() || !StringUtils.hasText(searchedPuuid)) {
            return Collections.emptyMap();
        }

        return matches.stream()
                .filter(match -> match != null && match.getInfo() != null && match.getInfo().getParticipants() != null)
                .flatMap(match -> match.getInfo().getParticipants().stream())
                .filter(participant -> participant != null && searchedPuuid.equals(participant.getPuuid()) && StringUtils.hasText(participant.getChampionName()))
                .collect(Collectors.groupingBy(ParticipantDto::getChampionName, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public List<SummonerSuggestionDTO> getSummonerSuggestions(String partialName, Map<String, SummonerSuggestionDTO> userHistoryDtoMap) {
        Stream<Map.Entry<String, SummonerSuggestionDTO>> stream;
        Map<String, SummonerSuggestionDTO> historyToUse = (userHistoryDtoMap != null) ? userHistoryDtoMap : Collections.emptyMap();

        if (!StringUtils.hasText(partialName)) {
            stream = historyToUse.entrySet().stream();
        } else {
            String lowerPartialName = partialName.toLowerCase();
            stream = historyToUse.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(lowerPartialName));
        }

        return stream
                .limit(10)
                .map(Map.Entry::getValue)
                .distinct()
                .collect(Collectors.toList());
    }

    public CompletableFuture<SummonerProfileData> getSummonerProfileDataAsync(String gameName, String tagLine) {
        return getSummonerByRiotId(gameName, tagLine)
                .thenCompose(summoner -> {
                    if (summoner == null || !StringUtils.hasText(summoner.getPuuid())) {
                        logger.warn("Summoner not found or PUUID is missing for {}#{}", gameName, tagLine);
                        return CompletableFuture.completedFuture(new SummonerProfileData("Summoner not found or PUUID missing."));
                    }

                    String displayRiotId = summoner.getName() + "#" + tagLine;
                    String iconUrl = riotApiClient.getProfileIconUrl(summoner.getProfileIconId());
                    SummonerSuggestionDTO suggestionDTO = new SummonerSuggestionDTO(displayRiotId, summoner.getProfileIconId(), summoner.getSummonerLevel());

                    // Fetch league entries (by PUUID) and match history concurrently
                    CompletableFuture<List<LeagueEntryDTO>> leagueEntriesFuture =
                            riotApiClient.getLeagueEntriesByPuuid(summoner.getPuuid())
                                    .thenApply(leagueEntries -> {
                                        if (leagueEntries != null && !leagueEntries.isEmpty()) {
                                            Instant now = Instant.now();
                                            playerLpRecordService.savePlayerLpRecords(summoner.getPuuid(), leagueEntries, now);
                                        }
                                        return leagueEntries;
                                    })
                                    .exceptionally(ex -> {
                                        logger.error("Error fetching league entries for puuid {}: {}",
                                                summoner.getPuuid(), ex.getMessage(), ex);
                                        return Collections.emptyList();
                                    });
                    // Load fewer matches initially for faster first render; client can request more via pagination
                    CompletableFuture<List<MatchV5Dto>> matchHistoryFuture = getMatchHistory(summoner.getPuuid(), 10);

                    return CompletableFuture.allOf(leagueEntriesFuture, matchHistoryFuture)
                            .thenApply(v -> {
                                List<LeagueEntryDTO> leagueEntries = leagueEntriesFuture.join();
                                List<MatchV5Dto> matchHistory = matchHistoryFuture.join();

                                playerLpRecordService.calculateAndSetLpChangesForMatches(summoner, matchHistory);

                                Map<String, Long> championPlayCounts = getChampionPlayCounts(matchHistory, summoner.getPuuid());

                                return new SummonerProfileData(summoner, leagueEntries, matchHistory, suggestionDTO, championPlayCounts, iconUrl);
                            });
                })
                .exceptionally(ex -> {
                    logger.error("Error building summoner profile data for {}#{}: {}", gameName, tagLine, ex.getMessage(), ex);
                    return new SummonerProfileData("An error occurred while fetching summoner profile data: " + ex.getMessage());
                });
    }

    /**
     * Fetch the currently authenticated user's summoner profile using an RSO Bearer token.
     * This uses the endpoint /lol/summoner/v4/summoners/me and does NOT require the X-Riot-Token.
     */
    public CompletableFuture<Summoner> getSummonerViaRso(String bearerToken) {
        if (!StringUtils.hasText(bearerToken)) {
            logger.error("Error: bearerToken is empty for RSO summoner request.");
            return CompletableFuture.completedFuture(null);
        }
        return riotApiClient.getSummonerMeWithBearer(bearerToken)
                .exceptionally(ex -> {
                    logger.error("Error fetching RSO summoner via /me: {}", ex.getMessage(), ex);
                    return null;
                });
    }

    private static String maskPuuid(String puuid) {
        if (puuid == null) return "(null)";
        int len = puuid.length();
        if (len <= 10) return "***";
        return puuid.substring(0, 6) + "..." + puuid.substring(len - 4);
    }
}
