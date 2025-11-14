// Package declaration: Defines that this class belongs to the service layer of the Riot API
package com.zerox80.riotapi.service;

// Import of the Riot API client for external API calls
import com.zerox80.riotapi.client.RiotApiClient;
// Import of all model classes (DTOs and entities)
import com.zerox80.riotapi.model.*;
// Import of the service for LP history management
import com.zerox80.riotapi.service.PlayerLpRecordService;
// Import for logging interface
import org.slf4j.Logger;
// Import for logger factory to instantiate loggers
import org.slf4j.LoggerFactory;
// Import for dependency injection
import org.springframework.beans.factory.annotation.Autowired;
// Import for property injection (unused in this service)
import org.springframework.beans.factory.annotation.Value;
// Import for cache annotation to cache method results
import org.springframework.cache.annotation.Cacheable;
// Import for service component annotation
import org.springframework.stereotype.Service;
// Import for transaction management (unused in this service)
import org.springframework.transaction.annotation.Transactional;
// Import for string utility functions like hasText()
import org.springframework.util.StringUtils;

// Import for I/O exceptions
import java.io.IOException;
// Import for timestamp management
import java.time.Instant;
// Import for list collections
import java.util.List;
// Import for collection utility methods like emptyList()
import java.util.Collections;
// Import for ArrayList implementation
import java.util.ArrayList;
// Import for map interface
import java.util.Map;
// Import for ordered map implementation
import java.util.LinkedHashMap;
// Import for asynchronous/non-blocking operations
import java.util.concurrent.CompletableFuture;
// Import for stream collectors (e.g., toList())
import java.util.stream.Collectors;
// Import for thread-safe map (unused)
import java.util.concurrent.ConcurrentHashMap;
// Import for stream API
import java.util.stream.Stream;
// Import for optional container (unused)
import java.util.Optional;
// Import for language/region settings
import java.util.Locale;
// Import for list utility functions (e.g., partition)
import com.zerox80.riotapi.util.ListUtils;


/**
 * Service class for Riot API operations and business logic.
 *
 * This service handles:
 * - Fetching summoner data by Riot ID
 * - Retrieving match history with pagination
 * - Loading league entries and tracking LP changes
 * - Building complete summoner profile data
 * - Champion play count statistics
 * - Summoner search suggestions
 *
 * All methods work asynchronously using CompletableFuture for non-blocking operations.
 */
@Service
public class RiotApiService {

    // Logger instance for logging in this service
    private static final Logger logger = LoggerFactory.getLogger(RiotApiService.class);

    // Client for HTTP calls to the Riot API
    private final RiotApiClient riotApiClient;

    // Service for LP history and LP change calculations
    private final PlayerLpRecordService playerLpRecordService;


    /**
     * Constructor with dependency injection through Spring.
     *
     * @param riotApiClient Injected Riot API client
     * @param playerLpRecordService Injected LP record service
     */
    @Autowired
    public RiotApiService(RiotApiClient riotApiClient,
                          PlayerLpRecordService playerLpRecordService) {
        this.riotApiClient = riotApiClient;
        this.playerLpRecordService = playerLpRecordService;
    }


    /**
     * Retrieves paginated match history for a player.
     *
     * @param puuid Player's PUUID
     * @param start Starting index for pagination
     * @param count Number of matches to fetch
     * @return CompletableFuture containing list of matches
     */
    public CompletableFuture<List<MatchV5Dto>> getMatchHistoryPaged(String puuid, int start, int count) {
        // Validation: Check if PUUID is not null/empty
        if (!StringUtils.hasText(puuid)) {
            logger.error("Error: PUUID cannot be empty when fetching match history.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        // Validation: Check if count is positive
        if (count <= 0) {
            logger.error("Error: Count must be positive.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        // Normalization: Set negative start index to 0
        if (start < 0) start = 0;

        // Final variables for lambda usage
        final int from = start;
        final int limit = count;
        logger.info("Fetching paged match IDs for PUUID: {}, start={}, count={}...", maskPuuid(puuid), from, limit);

        // Asynchronous API call to fetch match IDs
        return riotApiClient.getMatchIdsByPuuid(puuid, from, limit)
                .thenCompose(matchIds -> {
                    if (matchIds == null || matchIds.isEmpty()) {
                        return CompletableFuture.completedFuture(Collections.<MatchV5Dto>emptyList());
                    }
                    // Split match IDs into batches of 5 for parallel processing
                    List<List<String>> batches = ListUtils.partition(matchIds, 5);
                    // Fetch match details for each batch asynchronously
                    List<CompletableFuture<List<MatchV5Dto>>> batchFutures = batches.stream()
                            .map(this::fetchMatchBatch)
                            .collect(Collectors.toList());
                    // Wait for all batches to complete
                    CompletableFuture<Void> allDone = CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0]));
                    // Transform results after completion
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


    /**
     * Retrieves summoner data by Riot ID (game name + tag line).
     *
     * @param gameName Player's in-game name
     * @param tagLine Player's tag line (e.g., "EUW", "NA1")
     * @return CompletableFuture containing Summoner data, or null if not found
     */
    public CompletableFuture<Summoner> getSummonerByRiotId(String gameName, String tagLine) {
        // Trim leading/trailing whitespace from game name (null-safe)
        String trimmedGameName = gameName != null ? gameName.trim() : null;
        // Trim leading/trailing whitespace from tag line (null-safe)
        String trimmedTagLine = tagLine != null ? tagLine.trim() : null;

        // Validation: Check if both fields are not empty
        if (!StringUtils.hasText(trimmedGameName) || !StringUtils.hasText(trimmedTagLine)) {
            logger.error("Error: Game name and tag line cannot be empty.");
            return CompletableFuture.completedFuture(null);
        }

        // Normalization: Replace multiple spaces with single space
        String normalizedGameName = trimmedGameName.replaceAll("\\s+", " ");
        String normalizedTagLine = trimmedTagLine.replaceAll("\\s+", " ");
        // Convert to lowercase for cache consistency
        String cacheFriendlyGameName = normalizedGameName.toLowerCase(Locale.ROOT);
        String cacheFriendlyTagLine = normalizedTagLine.toLowerCase(Locale.ROOT);

        logger.info("Searching for account: {}#{}...", normalizedGameName, normalizedTagLine);

        // Asynchronous API call to fetch account data
        return riotApiClient.getAccountByRiotId(cacheFriendlyGameName, cacheFriendlyTagLine)
                .thenCompose(account -> {
                    // Check if account exists and has PUUID
                    if (account != null && StringUtils.hasText(account.getPuuid())) {
                        logger.info("Account found, PUUID: {}. Fetching summoner data...", maskPuuid(account.getPuuid()));
                        // Asynchronous API call to fetch summoner details via PUUID
                        return riotApiClient.getSummonerByPuuid(account.getPuuid())
                                .thenApply(summoner -> {
                                    if (summoner != null) {
                                        // Check if game name is present in account object
                                        if (StringUtils.hasText(account.getGameName())) {
                                            summoner.setName(account.getGameName());
                                        } else {
                                            // Fallback if game name is missing from account
                                            summoner.setName(normalizedGameName);
                                            logger.warn("Warning: gameName is missing from AccountDto for PUUID: {}. Using provided gameName for Summoner object.", maskPuuid(account.getPuuid()));
                                        }
                                    }
                                    return summoner;
                                });
                    } else {
                        logger.info("Account for {}#{} not found or PUUID is missing.", trimmedGameName, trimmedTagLine);
                        return CompletableFuture.completedFuture(null);
                    }
                }).exceptionally(ex -> {
                    logger.error("Error fetching summoner data for {}#{}: {}", normalizedGameName, normalizedTagLine, ex.getMessage(), ex);
                    return null;
                });
    }


    /**
     * Retrieves ranked league entries for a player by PUUID.
     *
     * @param puuid Player's PUUID
     * @return CompletableFuture containing list of league entries (may be empty)
     */
    public CompletableFuture<List<LeagueEntryDTO>> getLeagueEntries(String puuid) {
        // Validation: Check if PUUID is not null/empty
        if (!StringUtils.hasText(puuid)) {
            logger.error("Error: PUUID cannot be empty.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        logger.info("Fetching league entries (by PUUID) for PUUID: {}...", maskPuuid(puuid));

        // Asynchronous API call to fetch league entries via PUUID
        return riotApiClient.getLeagueEntriesByPuuid(puuid)
                .thenApply(leagueEntries -> {
                    // Check if league entries are present
                    if (leagueEntries != null && !leagueEntries.isEmpty()) {
                        // Create current timestamp for LP record storage
                        Instant now = Instant.now();
                        // Database operation: Save LP records for future LP change calculations
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


    /**
     * Retrieves match history for a player.
     * Results are cached to reduce API calls.
     *
     * @param puuid Player's PUUID
     * @param numberOfMatches Number of matches to fetch
     * @return CompletableFuture containing list of matches
     */
    @Cacheable(value = "matchHistory", key = "#puuid + '-' + #numberOfMatches")
    public CompletableFuture<List<MatchV5Dto>> getMatchHistory(String puuid, int numberOfMatches) {
        // Validation: Check if PUUID is not null/empty
        if (!StringUtils.hasText(puuid)) {
            logger.error("Error: PUUID cannot be empty when fetching match history.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        // Validation: Check if number of matches is positive
        if (numberOfMatches <= 0) {
            logger.error("Error: Number of matches to fetch must be positive.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        logger.info("Fetching last {} match IDs for PUUID: {}...", numberOfMatches, maskPuuid(puuid));

        // Asynchronous API call to fetch match IDs
        return riotApiClient.getMatchIdsByPuuid(puuid, numberOfMatches)
                .thenCompose(matchIds -> {
                    if (matchIds.isEmpty()) {
                        logger.info("No match IDs found for PUUID: {}", maskPuuid(puuid));
                        return CompletableFuture.completedFuture(Collections.<MatchV5Dto>emptyList());
                    }
                    logger.info("Fetching details for {} matches in batches...", matchIds.size());

                    // Split match IDs into batches of 5 for parallel processing (rate limiting protection)
                    List<List<String>> batches = ListUtils.partition(matchIds, 5);
                    // Fetch match details for each batch asynchronously
                    List<CompletableFuture<List<MatchV5Dto>>> batchFutures = batches.stream()
                            .map(this::fetchMatchBatch)
                            .collect(Collectors.toList());

                    // Wait for all batches to complete
                    CompletableFuture<Void> allDone = CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0]));
                    // Transform results after all batches complete
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

    /**
     * Private helper method to fetch a batch of match details asynchronously.
     *
     * @param matchIdBatch List of match IDs to fetch
     * @return CompletableFuture containing list of match details
     */
    private CompletableFuture<List<MatchV5Dto>> fetchMatchBatch(List<String> matchIdBatch) {
        // Stream over all match IDs in the batch
        List<CompletableFuture<MatchV5Dto>> matchDetailFutures = matchIdBatch.stream()
            .map(matchId -> riotApiClient.getMatchDetails(matchId)
                .exceptionally(ex -> {
                    logger.error("Error fetching details for match ID {}: {}", matchId, ex.getMessage(), ex);
                    return null;
                }))
            .collect(Collectors.toList());

        // Wait for all match detail futures to complete
        return CompletableFuture.allOf(matchDetailFutures.toArray(new CompletableFuture[0]))
            .thenApply(v -> matchDetailFutures.stream()
                .map(CompletableFuture::join)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }


    /**
     * Calculates champion play count statistics for a player.
     *
     * @param matches List of matches to analyze
     * @param searchedPuuid PUUID of the player to analyze
     * @return Map of champion name to play count, sorted by frequency
     */
    public Map<String, Long> getChampionPlayCounts(List<MatchV5Dto> matches, String searchedPuuid) {
        // Validation: Check if matches and PUUID are present
        if (matches == null || matches.isEmpty() || !StringUtils.hasText(searchedPuuid)) {
            return Collections.emptyMap();
        }

        return matches.stream()
                // Filter null matches and matches without participant data
                .filter(match -> match != null && match.getInfo() != null && match.getInfo().getParticipants() != null)
                // FlatMap: Create stream of all participants from all matches
                .flatMap(match -> match.getInfo().getParticipants().stream())
                // Filter for the searched player (PUUID match) and valid champion name
                .filter(participant -> participant != null && searchedPuuid.equals(participant.getPuuid()) && StringUtils.hasText(participant.getChampionName()))
                // Business logic: Group by champion name and count occurrences
                .collect(Collectors.groupingBy(ParticipantDto::getChampionName, Collectors.counting()))
                .entrySet().stream()
                // Sorting: By play count descending (most played first)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                // Collect to LinkedHashMap to preserve sort order
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }


    /**
     * Retrieves summoner suggestions based on search history.
     *
     * @param partialName Partial search text (may be empty)
     * @param userHistoryDtoMap Search history map
     * @return List of summoner suggestions (max 10)
     */
    public List<SummonerSuggestionDTO> getSummonerSuggestions(String partialName, Map<String, SummonerSuggestionDTO> userHistoryDtoMap) {
        Stream<Map.Entry<String, SummonerSuggestionDTO>> stream;
        // Null-safe: Use provided history or empty map
        Map<String, SummonerSuggestionDTO> historyToUse = (userHistoryDtoMap != null) ? userHistoryDtoMap : Collections.emptyMap();

        // Check if no search text was entered
        if (!StringUtils.hasText(partialName)) {
            stream = historyToUse.entrySet().stream();
        } else {
            // Normalize search text to lowercase
            String lowerPartialName = partialName.toLowerCase(Locale.ROOT);
            stream = historyToUse.entrySet().stream()
                    // Filter entries that start with search text (prefix matching)
                    .filter(entry -> entry.getKey().startsWith(lowerPartialName));
        }

        // Process filtered stream
        List<SummonerSuggestionDTO> out = stream
                // Business logic: Limit to maximum 10 suggestions (performance optimization)
                .limit(10)
                .map(Map.Entry::getValue)
                .distinct()
                .collect(Collectors.toList());

        // Enrich missing icon URLs to satisfy frontend expectation
        out.forEach(dto -> {
            // Check if DTO exists but icon URL is missing
            if (dto != null && !StringUtils.hasText(dto.getProfileIconUrl())) {
                int iconId = dto.getProfileIconId();
                // Check if icon ID is invalid
                if (iconId <= 0) {
                    dto.setProfileIconUrl(null);
                    return;
                }
                try {
                    // Business logic: Generate icon URL from icon ID via client
                    dto.setProfileIconUrl(riotApiClient.getProfileIconUrl(iconId));
                } catch (Exception ignored) {}
            }
        });
        return out;
    }


    /**
     * Retrieves complete summoner profile data including matches and league entries.
     *
     * @param gameName Player's in-game name
     * @param tagLine Player's tag line
     * @return CompletableFuture containing complete profile data
     */
    public CompletableFuture<SummonerProfileData> getSummonerProfileDataAsync(String gameName, String tagLine) {
        // Asynchronous call: First fetch basic summoner data
        return getSummonerByRiotId(gameName, tagLine)
                .thenCompose(summoner -> {
                    // Validation: Check if summoner found and PUUID present
                    if (summoner == null || !StringUtils.hasText(summoner.getPuuid())) {
                        logger.warn("Summoner not found or PUUID is missing for {}#{}", gameName, tagLine);
                        return CompletableFuture.completedFuture(new SummonerProfileData("Summoner not found or PUUID missing."));
                    }

                    // Business logic: Create display name in format Name#Tag
                    String displayRiotId = summoner.getName() + "#" + tagLine;
                    // Generate profile icon URL
                    String iconUrl = riotApiClient.getProfileIconUrl(summoner.getProfileIconId());
                    // Create suggestion DTO for search history
                    SummonerSuggestionDTO suggestionDTO = new SummonerSuggestionDTO(displayRiotId, summoner.getProfileIconId(), summoner.getSummonerLevel(), iconUrl);

                    // Fetch league entries (by PUUID) and match history concurrently
                    CompletableFuture<List<LeagueEntryDTO>> leagueEntriesFuture =
                            riotApiClient.getLeagueEntriesByPuuid(summoner.getPuuid())
                                    .thenApply(leagueEntries -> {
                                        // Check if league data present
                                        if (leagueEntries != null && !leagueEntries.isEmpty()) {
                                            Instant now = Instant.now();
                                            // Database operation: Save LP snapshot for LP tracking
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

                    // Combine both parallel futures (league + matches)
                    return CompletableFuture.allOf(leagueEntriesFuture, matchHistoryFuture)
                            .thenApply(v -> {
                                // Extract league results (blocking but safe)
                                List<LeagueEntryDTO> leagueEntries = leagueEntriesFuture.join();
                                // Extract match results (blocking but safe)
                                List<MatchV5Dto> matchHistory = matchHistoryFuture.join();

                                // Business logic: Calculate LP changes for each match based on saved LP records
                                playerLpRecordService.calculateAndSetLpChangesForMatches(summoner, matchHistory);

                                // Business logic: Calculate champion statistics
                                Map<String, Long> championPlayCounts = getChampionPlayCounts(matchHistory, summoner.getPuuid());

                                // Create complete profile data object
                                return new SummonerProfileData(summoner, leagueEntries, matchHistory, suggestionDTO, championPlayCounts, iconUrl);
                            });
                })
                .exceptionally(ex -> {
                    logger.error("Error building summoner profile data for {}#{}: {}", gameName, tagLine, ex.getMessage(), ex);
                    return new SummonerProfileData("An error occurred while fetching summoner profile data: " + ex.getMessage());
                });
    }


    /**
     * Retrieves summoner data via RSO (Riot Sign-On) Bearer token.
     *
     * @param bearerToken RSO Bearer token from OAuth flow
     * @return CompletableFuture containing authenticated summoner data
     */
    public CompletableFuture<Summoner> getSummonerViaRso(String bearerToken) {
        // Validation: Check if bearer token is present
        if (!StringUtils.hasText(bearerToken)) {
            logger.error("Error: bearerToken is empty for RSO summoner request.");
            return CompletableFuture.completedFuture(null);
        }
        // Asynchronous API call: Fetch summoner data via RSO token (OAuth authentication)
        return riotApiClient.getSummonerMeWithBearer(bearerToken)
                .exceptionally(ex -> {
                    logger.error("Error fetching RSO summoner via /me: {}", ex.getMessage(), ex);
                    return null;
                });
    }

    /**
     * Masks PUUID for logging to protect sensitive data.
     * Shows first 6 and last 4 characters.
     *
     * @param puuid The PUUID to mask
     * @return Masked PUUID string (e.g., "abc123...xyz9")
     */
    private static String maskPuuid(String puuid) {
        if (puuid == null) return "(null)";
        int len = puuid.length();
        if (len <= 10) return "***";
        return puuid.substring(0, 6) + "..." + puuid.substring(len - 4);
    }
}
