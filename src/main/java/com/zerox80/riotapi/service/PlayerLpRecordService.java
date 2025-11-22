// Package declaration: Defines that this class belongs to the service layer
package com.zerox80.riotapi.service;

// Import for league entry DTO
import com.zerox80.riotapi.model.LeagueEntryDTO;
// Import for match data DTO
import com.zerox80.riotapi.model.MatchV5Dto;
// Import for LP record entity
import com.zerox80.riotapi.model.PlayerLpRecord;
// Import for summoner DTO
import com.zerox80.riotapi.model.Summoner;
// Import for database repository to store LP records
import com.zerox80.riotapi.repository.PlayerLpRecordRepository;
// Import for logging interface
import org.slf4j.Logger;
// Import for logger factory to instantiate loggers
import org.slf4j.LoggerFactory;
// Import for dependency injection
import org.springframework.beans.factory.annotation.Autowired;
// Import for service component annotation
import org.springframework.stereotype.Service;
// Import for transaction management in database operations
import org.springframework.transaction.annotation.Transactional;
// Import for string utility functions
import org.springframework.util.StringUtils;

// Import for time duration calculations
import java.time.Duration;
// Import for timestamp management
import java.time.Instant;
// Import for ArrayList implementation

// Import for collection utility methods
import java.util.Collections;
// Import for HashMap implementation
import java.util.HashMap;
// Import for HashSet implementation
import java.util.HashSet;
// Import for list collections
import java.util.List;
// Import for map interface
import java.util.Map;
// Import for object utility methods (e.g., equals)
import java.util.Objects;
// Import for set interface
import java.util.Set;

/**
 * Service class for LP (League Points) record management and LP change
 * calculations.
 *
 * Stores snapshots of player LP after each match and calculates LP gains/losses
 * by comparing records before and after match timestamps. Supports both SoloQ
 * and Flex queue types.
 */
@Service
public class PlayerLpRecordService {

    // Logger instance for logging in this service
    private static final Logger logger = LoggerFactory.getLogger(PlayerLpRecordService.class);
    // Constant: Tolerance of 10 minutes for LP record matching (if timestamps
    // slightly differ)
    private static final Duration MATCH_END_TOLERANCE = Duration.ofMinutes(10);
    // Database repository for LP record persistence
    private final PlayerLpRecordRepository playerLpRecordRepository;

    /**
     * Constructor with repository injection.
     *
     * @param playerLpRecordRepository Injected LP record repository
     */
    @Autowired
    public PlayerLpRecordService(PlayerLpRecordRepository playerLpRecordRepository) {
        this.playerLpRecordRepository = playerLpRecordRepository;
    }

    /**
     * Public method to save LP records to database.
     *
     * Creates a snapshot of the player's current LP for each ranked queue type
     * (SoloQ and Flex). These snapshots are used later to calculate LP changes
     * between matches.
     *
     * @param puuid         Player's PUUID
     * @param leagueEntries List of league entries (SoloQ, FlexQ, etc.)
     * @param timestamp     Timestamp of the snapshot
     */
    @Transactional
    public void savePlayerLpRecords(String puuid, List<LeagueEntryDTO> leagueEntries, Instant timestamp) {
        // Null-safe: Use provided timestamp or current time
        Instant ts = timestamp != null ? timestamp : Instant.now();
        // Iterate over all league entries (SoloQ, FlexQ, etc.)
        for (LeagueEntryDTO entry : leagueEntries) {
            // Filter only ranked queues (SoloQ and FlexQ)
            if ("RANKED_SOLO_5x5".equals(entry.getQueueType()) || "RANKED_FLEX_SR".equals(entry.getQueueType())) {
                // Create new LP record entity
                PlayerLpRecord record = new PlayerLpRecord(
                        puuid,
                        entry.getQueueType(),
                        ts,
                        entry.getLeaguePoints(),
                        entry.getTier(),
                        entry.getRank());
                // Database operation: Save LP record to database
                playerLpRecordRepository.save(record);
                // Debug logging for saved record
                logger.debug("Saved LP record for puuid {}, queue {}: {} LP at {}", maskPuuid(puuid),
                        entry.getQueueType(), entry.getLeaguePoints(), ts);
            }
        }
    }

    /**
     * Public method to calculate and assign LP changes to matches (business logic).
     *
     * For each ranked match, finds the LP record before and after the match end
     * time,
     * calculates the difference, and sets the lpChange field in the match info.
     * Handles edge cases like tier/rank changes (promotions/demotions) and missing
     * records.
     *
     * @param summoner     The summoner whose matches to process
     * @param matchHistory List of matches to calculate LP changes for
     */
    public void calculateAndSetLpChangesForMatches(Summoner summoner, List<MatchV5Dto> matchHistory) {
        // Validation: Check if all required data is present
        if (summoner == null || !StringUtils.hasText(summoner.getPuuid()) || matchHistory == null
                || matchHistory.isEmpty()) {
            return;
        }

        // Database operation: Preload all relevant LP records from database
        // (performance optimization)
        Map<String, List<PlayerLpRecord>> recordsByQueue = preloadRankedRecords(summoner.getPuuid(), matchHistory);

        // Iterate over all matches in history
        for (MatchV5Dto match : matchHistory) {
            // Check if match data is complete
            if (match == null || match.getInfo() == null) {
                continue;
            }

            // Business logic: Convert queue ID to queue type string (420->RANKED_SOLO_5x5)
            String queueTypeForDbQuery = resolveRankedQueueType(match.getInfo().getQueueId());
            // Check if queue is a ranked mode
            if (queueTypeForDbQuery == null) {
                continue;
            }

            // Get match end timestamp in milliseconds
            long endMillis = match.getInfo().getGameEndTimestamp();
            // Validation: Check if timestamp is present
            if (endMillis <= 0L) {
                logger.debug("Skipping LP change for match {} due to missing/invalid gameEndTimestamp.",
                        safeMatchId(match));
                continue;
            }
            // Convert milliseconds to Instant object
            Instant matchEndTime = Instant.ofEpochMilli(endMillis);

            try {
                // Fetch preloaded LP records for this queue
                List<PlayerLpRecord> records = recordsByQueue.getOrDefault(queueTypeForDbQuery,
                        Collections.emptyList());
                // Check if LP history is available
                if (records.isEmpty()) {
                    logger.debug("No LP history available for puuid {} queue {}.", maskPuuid(summoner.getPuuid()),
                            queueTypeForDbQuery);
                    continue;
                }

                // Business logic: Find LP record BEFORE match end (binary search)
                PlayerLpRecord recordBefore = findRecordBefore(records, matchEndTime);
                // Business logic: Find LP record AFTER match end (binary search)
                PlayerLpRecord recordAfter = findRecordAfter(records, matchEndTime);

                // Validation: Check if both records were found
                if (recordBefore == null || recordAfter == null) {
                    logger.debug(
                            "LP records before or after match {} not found for PUUID {} and queue {}. Cannot calculate LP change.",
                            safeMatchId(match), maskPuuid(summoner.getPuuid()), queueTypeForDbQuery);
                    continue;
                }

                // Check if "after" record is actually after match end
                if (!recordAfter.getTimestamp().isAfter(matchEndTime)) {
                    // Calculate time difference
                    Duration gap = Duration.between(recordAfter.getTimestamp(), matchEndTime);
                    // Check if difference exceeds tolerance threshold (>10 minutes)
                    if (gap.compareTo(MATCH_END_TOLERANCE) > 0) {
                        logger.debug(
                                "LP record after match {} for PUUID {} (queue {}) is {} minutes before match end time {} – skipping.",
                                safeMatchId(match), maskPuuid(summoner.getPuuid()), queueTypeForDbQuery,
                                gap.toMinutes(), matchEndTime);
                        continue;
                    }
                }

                // Get LP before match
                int lpBefore = recordBefore.getLeaguePoints();
                // Get LP after match
                int lpAfter = recordAfter.getLeaguePoints();
                // Business logic: Calculate LP difference (can be positive/negative/0)
                int lpChange = lpAfter - lpBefore;

                // Check if tier/rank changed (promotion/demotion)
                if (!Objects.equals(recordBefore.getTier(), recordAfter.getTier())
                        || !Objects.equals(recordBefore.getRank(), recordAfter.getRank())) {
                    logger.warn(
                            "Tier/Rank changed for match {}. PUUID: {}. Before: {} {} {} LP, After: {} {} {} LP. LP Change calculation might be inaccurate or represent promotion/demotion.",
                            safeMatchId(match), maskPuuid(summoner.getPuuid()),
                            recordBefore.getTier(), recordBefore.getRank(), recordBefore.getLeaguePoints(),
                            recordAfter.getTier(), recordAfter.getRank(), recordAfter.getLeaguePoints());
                    // Set LP change to null for promotion/demotion (inaccurate)
                    match.getInfo().setLpChange(null);
                } else {
                    // Tier/rank unchanged - normal LP change
                    // Business logic: Set calculated LP change in match object
                    match.getInfo().setLpChange(lpChange);
                }
            } catch (Exception e) {
                // Log error for individual match
                logger.error("Error calculating LP change for match {} PUUID {}: {}", safeMatchId(match),
                        maskPuuid(summoner.getPuuid()), e.getMessage(), e);
            }
        }
    }

    /**
     * Private method to preload all relevant LP records from database (performance
     * optimization).
     *
     * Scans match history to determine which queue types are needed, then fetches
     * all LP records for those queues in bulk. Records are sorted ascending by
     * timestamp
     * for efficient binary search.
     *
     * @param puuid        Player's PUUID
     * @param matchHistory List of matches to process
     * @return Map of queue type to sorted list of LP records
     */
    private Map<String, List<PlayerLpRecord>> preloadRankedRecords(String puuid, List<MatchV5Dto> matchHistory) {
        // Set to collect all required queue types (no duplicates)
        Set<String> queues = new HashSet<>();
        long minTs = Long.MAX_VALUE;
        long maxTs = Long.MIN_VALUE;

        // Iterate over all matches
        for (MatchV5Dto match : matchHistory) {
            // Check if match data is present
            if (match == null || match.getInfo() == null) {
                continue;
            }
            // Convert queue ID to queue type
            String queue = resolveRankedQueueType(match.getInfo().getQueueId());
            // Check if it's a ranked queue
            if (queue != null) {
                // Add queue type to set (duplicates automatically ignored)
                queues.add(queue);
                long end = match.getInfo().getGameEndTimestamp();
                if (end > 0) {
                    if (end < minTs)
                        minTs = end;
                    if (end > maxTs)
                        maxTs = end;
                }
            }
        }

        // Create cache map for LP records per queue
        Map<String, List<PlayerLpRecord>> cache = new HashMap<>();

        if (queues.isEmpty() || minTs == Long.MAX_VALUE) {
            return cache;
        }

        // Add buffer (e.g., 24 hours) to ensure we catch records just before/after
        Instant start = Instant.ofEpochMilli(minTs).minus(Duration.ofHours(24));
        Instant end = Instant.ofEpochMilli(maxTs).plus(Duration.ofHours(24));

        // Iterate over all required queues
        for (String queue : queues) {
            try {
                // Database operation: Load LP records for PUUID and queue within time range
                List<PlayerLpRecord> records = playerLpRecordRepository
                        .findByPuuidAndQueueTypeAndTimestampBetweenOrderByTimestampAsc(puuid, queue, start, end);
                // Store list in cache
                cache.put(queue, records);
            } catch (Exception e) {
                // Log database error
                logger.error("Failed to preload LP records for puuid {} queue {}: {}", maskPuuid(puuid), queue,
                        e.getMessage(), e);
                // Store empty list on error (fallback)
                cache.put(queue, Collections.emptyList());
            }
        }
        // Return populated cache
        return cache;
    }

    /**
     * Private method to convert queue ID to queue type string.
     *
     * @param queueId The queue identifier (420, 440, etc.)
     * @return Queue type string (RANKED_SOLO_5x5, RANKED_FLEX_SR) or null for
     *         non-ranked
     */
    private String resolveRankedQueueType(int queueId) {
        // Switch expression for queue ID mapping
        return switch (queueId) {
            case 420 -> "RANKED_SOLO_5x5";
            case 440 -> "RANKED_FLEX_SR";
            default -> null;
        };
    }

    /**
     * Private method to find the last LP record BEFORE a timestamp (binary search).
     *
     * Uses binary search on a sorted list to efficiently find the most recent
     * LP record that occurred before the given timestamp.
     *
     * @param records   List of LP records sorted by timestamp ascending
     * @param timestamp The timestamp to search for
     * @return Last record before timestamp, or null if none found
     */
    private PlayerLpRecord findRecordBefore(List<PlayerLpRecord> records, Instant timestamp) {
        // Lower bound for binary search
        int low = 0;
        // Upper bound for binary search
        int high = records.size() - 1;
        // Candidate for best match
        PlayerLpRecord candidate = null;
        // Binary search loop
        while (low <= high) {
            // Calculate middle index (>>> 1 = division by 2, overflow-safe)
            int mid = (low + high) >>> 1;
            // Get record at middle position
            PlayerLpRecord current = records.get(mid);
            // Check if record is before timestamp
            if (current.getTimestamp().isBefore(timestamp)) {
                // Update candidate (closer to searched timestamp)
                candidate = current;
                // Search in upper half
                low = mid + 1;
            } else {
                // Record is after or exactly at timestamp
                // Search in lower half
                high = mid - 1;
            }
        }
        // Return last record before timestamp (or null if none found)
        return candidate;
    }

    /**
     * Private method to find the first LP record AFTER a timestamp (binary search).
     *
     * Uses binary search on a sorted list to efficiently find the earliest
     * LP record that occurred after the given timestamp.
     *
     * @param records   List of LP records sorted by timestamp ascending
     * @param timestamp The timestamp to search for
     * @return First record after timestamp, or null if none found
     */
    private PlayerLpRecord findRecordAfter(List<PlayerLpRecord> records, Instant timestamp) {
        // Lower bound for binary search
        int low = 0;
        // Upper bound for binary search
        int high = records.size() - 1;
        // Candidate for best match
        PlayerLpRecord candidate = null;
        // Binary search loop
        while (low <= high) {
            // Calculate middle index (>>> 1 = division by 2, overflow-safe)
            int mid = (low + high) >>> 1;
            // Get record at middle position
            PlayerLpRecord current = records.get(mid);
            // Check if record is before timestamp
            if (current.getTimestamp().isBefore(timestamp)) {
                // Search in upper half
                low = mid + 1;
            } else {
                // Record is after or exactly at timestamp
                // Update candidate (closer to searched timestamp)
                candidate = current;
                // Search in lower half
                high = mid - 1;
            }
        }
        // Return first record after timestamp (or null if none found)
        return candidate;
    }

    /**
     * Private utility method to mask PUUIDs for logging (privacy protection).
     *
     * Shows first 6 and last 4 characters of the PUUID, masking the middle
     * to protect player identity in log files.
     *
     * @param puuid The PUUID to mask
     * @return Masked PUUID (e.g., "abc123...xyz9")
     */
    private static String maskPuuid(String puuid) {
        // Return "(null)" for null values
        if (puuid == null)
            return "(null)";
        // Get length of PUUID
        int len = puuid.length();
        // Return "***" for very short PUUIDs (full masking)
        if (len <= 10)
            return "***";
        // Mask PUUID: Show first 6 and last 4 characters (e.g., "abc123...xyz9")
        return puuid.substring(0, 6) + "..." + puuid.substring(len - 4);
    }

    /**
     * Private utility method to safely extract match ID for logging.
     *
     * @param match The match DTO
     * @return Match ID or "(unknown)" if not available
     */
    private static String safeMatchId(MatchV5Dto match) {
        try {
            // Check if all required objects are present
            if (match != null && match.getMetadata() != null && match.getMetadata().getMatchId() != null) {
                // Return match ID
                return match.getMetadata().getMatchId();
            }
        } catch (Exception ignored) {
        }
        // Return fallback string if match ID not available
        return "(unknown)";
    }
}
