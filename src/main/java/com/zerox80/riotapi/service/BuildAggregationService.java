// Package declaration: Defines that this class belongs to the service layer of the Riot API
package com.zerox80.riotapi.service;

// Import of the Riot API client for external API calls
import com.zerox80.riotapi.client.RiotApiClient;
// Import for champion build response DTO
import com.zerox80.riotapi.dto.ChampionBuildDto;
// Import for item statistics DTO
import com.zerox80.riotapi.dto.ItemStatDto;
// Import for rune statistics DTO
import com.zerox80.riotapi.dto.RuneStatDto;
// Import for summoner spell pair statistics DTO
import com.zerox80.riotapi.dto.SpellPairStatDto;
// Import of all model classes (DTOs and entities)
import com.zerox80.riotapi.model.*;
// Import for item statistics repository (database)
import com.zerox80.riotapi.repository.ChampionItemStatRepository;
// Import for rune statistics repository (database)
import com.zerox80.riotapi.repository.ChampionRuneStatRepository;
// Import for spell statistics repository (database)
import com.zerox80.riotapi.repository.ChampionSpellPairStatRepository;
// Import for logging interface
import org.slf4j.Logger;
// Import for logger factory to instantiate loggers
import org.slf4j.LoggerFactory;
// Import for asynchronous method execution
import org.springframework.scheduling.annotation.Async;
// Import for service component annotation
import org.springframework.stereotype.Service;
// Import for programmatic transaction management
import org.springframework.transaction.PlatformTransactionManager;
// Import for transactional code blocks
import org.springframework.transaction.support.TransactionTemplate;
// Import for string utility functions
import org.springframework.util.StringUtils;

// Import for time duration calculations
import java.time.Duration;
// Import of all Java utility classes
import java.util.*;
// Import for asynchronous/non-blocking operations
import java.util.concurrent.CompletableFuture;
// Import for exception handling in CompletableFutures
import java.util.concurrent.CompletionException;
// Import for thread-safe integer operations
import java.util.concurrent.atomic.AtomicInteger;
// Import for supplier functional interface
import java.util.function.BooleanSupplier;
// Import for stream collectors
import java.util.stream.Collectors;
// Import for future exception handling
import java.util.concurrent.ExecutionException;
// Import for time units
import java.util.concurrent.TimeUnit;
// Import for timeout exceptions
import java.util.concurrent.TimeoutException;


/**
 * Service class for aggregating champion build data from high-ELO matches.
 *
 * Analyzes match data from Emerald/Diamond players to determine popular item builds,
 * runes, and summoner spells for each champion. Supports role-specific aggregation
 * and stores results in the database for fast retrieval.
 */
@Service
public class BuildAggregationService {

    // Logger instance for logging in this service
    private static final Logger log = LoggerFactory.getLogger(BuildAggregationService.class);

    // Client for HTTP calls to the Riot API
    private final RiotApiClient riot;
    // Service for static data (champion keys, item names, etc.)
    private final DataDragonService dd;
    // Database repository for item build statistics
    private final ChampionItemStatRepository itemRepo;
    // Database repository for rune statistics
    private final ChampionRuneStatRepository runeRepo;
    // Database repository for summoner spell statistics
    private final ChampionSpellPairStatRepository spellRepo;
    // Template for programmatic transaction management
    private final TransactionTemplate transactionTemplate;


    /**
     * Constructor with dependency injection of all required services/repositories.
     *
     * @param riot Injected Riot API client
     * @param dd Injected DataDragon service
     * @param itemRepo Injected item repository
     * @param runeRepo Injected rune repository
     * @param spellRepo Injected spell repository
     * @param transactionManager Injected transaction manager for database transactions
     */
    public BuildAggregationService(RiotApiClient riot,
                                   DataDragonService dd,
                                   ChampionItemStatRepository itemRepo,
                                   ChampionRuneStatRepository runeRepo,
                                   ChampionSpellPairStatRepository spellRepo,
                                   PlatformTransactionManager transactionManager) {
        this.riot = riot;
        this.dd = dd;
        this.itemRepo = itemRepo;
        this.runeRepo = runeRepo;
        this.spellRepo = spellRepo;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Private helper method to fetch top 10 item statistics from database.
     *
     * @param championId The champion identifier
     * @param patch The game patch version (e.g., "15.18")
     * @param queueId The queue type (420=SoloQ, 440=Flex)
     * @param role The lane role (TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY, ALL)
     * @return List of top 10 item statistics sorted by count descending
     */
    private List<ChampionItemStat> fetchItems(String championId, String patch, int queueId, String role) {
        // Check if specific role is specified (e.g., "TOP", "JUNGLE")
        if (StringUtils.hasText(role) && !"ALL".equals(role)) {
            // Database query: Fetch top 10 items for champion+role+patch+queue sorted by frequency
            return itemRepo.findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(championId, role, patch, queueId);
        }
        // Database query: Fetch top 10 items for champion+patch+queue (all roles aggregated)
        return itemRepo.findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(championId, patch, queueId);
    }

    /**
     * Private helper method to fetch top 10 rune statistics from database.
     *
     * @param championId The champion identifier
     * @param patch The game patch version
     * @param queueId The queue type
     * @param role The lane role
     * @return List of top 10 rune statistics sorted by count descending
     */
    private List<ChampionRuneStat> fetchRunes(String championId, String patch, int queueId, String role) {
        // Check if specific role is specified
        if (StringUtils.hasText(role) && !"ALL".equals(role)) {
            // Database query: Fetch top 10 rune combinations for champion+role+patch+queue
            return runeRepo.findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(championId, role, patch, queueId);
        }
        // Database query: Fetch top 10 rune combinations for champion+patch+queue (all roles)
        return runeRepo.findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(championId, patch, queueId);
    }

    /**
     * Private helper method to fetch top 10 summoner spell statistics from database.
     *
     * @param championId The champion identifier
     * @param patch The game patch version
     * @param queueId The queue type
     * @param role The lane role
     * @return List of top 10 summoner spell pairs sorted by count descending
     */
    private List<ChampionSpellPairStat> fetchSpells(String championId, String patch, int queueId, String role) {
        // Check if specific role is specified
        if (StringUtils.hasText(role) && !"ALL".equals(role)) {
            // Database query: Fetch top 10 summoner spell pairs for champion+role+patch+queue
            return spellRepo.findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(championId, role, patch, queueId);
        }
        // Database query: Fetch top 10 summoner spell pairs for champion+patch+queue (all roles)
        return spellRepo.findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(championId, patch, queueId);
    }


    /**
     * Public method to load aggregated champion build data from database.
     *
     * Retrieves item, rune, and summoner spell statistics for a champion and enriches
     * them with names, icons, and URLs from DataDragon. If role-specific data is not
     * available, falls back to aggregated data across all roles.
     *
     * @param championId The champion identifier (e.g., "Anivia")
     * @param queueId The queue type (420=SoloQ, 440=Flex, null defaults to 420)
     * @param role The lane role (TOP, JUNGLE, MID, ADC, SUPPORT, ALL)
     * @param locale The locale for item/rune names
     * @return Champion build DTO with enriched items, runes, and spells
     */
    public ChampionBuildDto loadBuild(String championId, Integer queueId, String role, Locale locale) {
        // Fetch current patch (e.g., "15.18")
        String patch = dd.getLatestShortPatch();
        // Default to SoloQ if queue ID is null
        int q = (queueId == null) ? 420 : queueId;
        // Normalize role to uppercase or "ALL"
        String requestedRole = (StringUtils.hasText(role) ? role.toUpperCase(Locale.ROOT) : "ALL");

        // Database query: Fetch item statistics for requested role
        List<ChampionItemStat> items = fetchItems(championId, patch, q, requestedRole);
        // Database query: Fetch rune statistics for requested role
        List<ChampionRuneStat> runes = fetchRunes(championId, patch, q, requestedRole);
        // Database query: Fetch spell statistics for requested role
        List<ChampionSpellPairStat> spells = fetchSpells(championId, patch, q, requestedRole);

        // Initialize effective role with requested role
        String effectiveRole = requestedRole;
        // Business logic: Check if no data available for specific role
        if (!"ALL".equals(requestedRole) && items.isEmpty() && runes.isEmpty() && spells.isEmpty()) {
            // Fallback: Use aggregated data across all roles
            effectiveRole = "ALL";
            // Database query: Fetch item statistics for all roles
            items = fetchItems(championId, patch, q, effectiveRole);
            // Database query: Fetch rune statistics for all roles
            runes = fetchRunes(championId, patch, q, effectiveRole);
            // Database query: Fetch spell statistics for all roles
            spells = fetchSpells(championId, patch, q, effectiveRole);
        }

        // Temporary variables for item lookup map (for final assignment)
        Map<Integer, ItemSummary> itemLookupTmp;
        // Temporary variable for spell lookup map
        Map<Integer, SummonerSpellInfo> spellLookupTmp;
        // Temporary variable for rune perk lookup map
        Map<Integer, RunePerkInfo> runePerkLookupTmp;
        // Temporary variable for rune style names map
        Map<Integer, String> styleNamesTmp;
        try {
            // Fetch item data from DataDragon (names, icons)
            itemLookupTmp = dd.getItems(locale);
            // Fetch summoner spell data from DataDragon
            spellLookupTmp = dd.getSummonerSpells(locale);
            // Fetch rune perk data from DataDragon
            runePerkLookupTmp = dd.getRunePerkLookup(locale);
            // Fetch rune style names from DataDragon
            styleNamesTmp = dd.getRuneStyleNames(locale);
        } catch (Exception e) {
            // Log warning on DataDragon error
            log.warn("DDragon lookups failed: {}", e.toString());
            // Fallback: Empty map for items
            itemLookupTmp = Collections.emptyMap();
            // Fallback: Empty map for spells
            spellLookupTmp = Collections.emptyMap();
            // Fallback: Empty map for rune perks
            runePerkLookupTmp = Collections.emptyMap();
            // Fallback: Empty map for style names
            styleNamesTmp = Collections.emptyMap();
        }
        // Final assignment for lambda usage
        final Map<Integer, ItemSummary> itemLookup = itemLookupTmp;
        // Final assignment for lambda usage
        final Map<Integer, SummonerSpellInfo> spellLookup = spellLookupTmp;
        // Final assignment for lambda usage
        final Map<Integer, RunePerkInfo> runePerkLookup = runePerkLookupTmp;
        // Final assignment for lambda usage
        final Map<Integer, String> styleNames = styleNamesTmp;
        // Fetch base URLs for images from DataDragon
        final Map<String,String> bases = dd.getImageBases(null);

        // Business logic: Transform item statistic entities to DTOs with enriched data
        List<ItemStatDto> itemDtos = items.stream().map(s -> {
            // Fetch item info from lookup map
            ItemSummary info = itemLookup.get(s.getItemId());
            // Use item name or fallback "Item {ID}"
            String name = info != null ? info.getName() : ("Item " + s.getItemId());
            // Generate icon URL
            String icon = info != null ? bases.get("item") + info.getImageFull() : null;
            // Create DTO with statistics and metadata
            return new ItemStatDto(s.getItemId(), s.getCount(), s.getWins(), name, icon);
        }).collect(Collectors.toList());

        // Business logic: Transform rune statistic entities to DTOs with enriched data
        List<RuneStatDto> runeDtos = runes.stream().map(s -> {
            // Fetch primary style name or fallback to ID
            String pName = styleNames.getOrDefault(s.getPrimaryStyle(), String.valueOf(s.getPrimaryStyle()));
            // Fetch sub style name or fallback to ID
            String sName = styleNames.getOrDefault(s.getSubStyle(), String.valueOf(s.getSubStyle()));
            // Fetch keystone info from lookup map
            RunePerkInfo k = runePerkLookup.get(s.getKeystone());
            // Use keystone name or fallback to ID
            String kName = k != null ? k.getName() : String.valueOf(s.getKeystone());
            // Generate keystone icon URL
            String kIcon = k != null ? ("https://ddragon.leagueoflegends.com/cdn/img/" + k.getIconFull()) : null;
            // Create DTO with statistics and metadata
            return new RuneStatDto(s.getPrimaryStyle(), s.getSubStyle(), s.getKeystone(), s.getCount(), s.getWins(), pName, sName, kName, kIcon);
        }).collect(Collectors.toList());

        // Business logic: Transform spell statistic entities to DTOs with enriched data
        List<SpellPairStatDto> spellDtos = spells.stream().map(s -> {
            // Fetch info for spell 1 from lookup map
            SummonerSpellInfo s1 = spellLookup.get(s.getSpell1Id());
            // Fetch info for spell 2 from lookup map
            SummonerSpellInfo s2 = spellLookup.get(s.getSpell2Id());
            // Use spell 1 name or fallback to ID
            String s1Name = s1 != null ? s1.getName() : String.valueOf(s.getSpell1Id());
            // Use spell 2 name or fallback to ID
            String s2Name = s2 != null ? s2.getName() : String.valueOf(s.getSpell2Id());
            // Generate spell 1 icon URL
            String s1Icon = s1 != null ? bases.get("spell") + s1.getImageFull() : null;
            // Generate spell 2 icon URL
            String s2Icon = s2 != null ? bases.get("spell") + s2.getImageFull() : null;
            // Create DTO with statistics and metadata
            return new SpellPairStatDto(s.getSpell1Id(), s.getSpell2Id(), s.getCount(), s.getWins(), s1Name, s2Name, s1Icon, s2Icon);
        }).collect(Collectors.toList());

        // Create final champion build DTO with all enriched statistics
        return new ChampionBuildDto(championId, patch, q, effectiveRole, itemDtos, runeDtos, spellDtos);
    }


    /**
     * Asynchronously aggregates champion build data from high-ELO matches.
     *
     * Fetches league entries from Emerald/Diamond tiers, resolves summoner PUUIDs,
     * retrieves match histories, and aggregates item/rune/spell statistics per role.
     * Results are stored in the database, replacing any existing data for the same
     * champion/patch/queue combination.
     *
     * @param championId The champion identifier (e.g., "Anivia")
     * @param queueId The queue type (420=SoloQ, 440=Flex, null defaults to 420)
     * @param pagesToScan Number of league entry pages to scan per tier/division
     * @param matchesPerSummoner Number of matches to analyze per summoner
     * @param maxSummoners Maximum number of summoners to collect
     * @param locale Locale for static data lookups
     */
    @Async("appTaskExecutor")
    public void aggregateChampion(String championId,
                                  Integer queueId,
                                  int pagesToScan,
                                  int matchesPerSummoner,
                                  int maxSummoners,
                                  Locale locale) {
        long t0 = System.currentTimeMillis();
        String patch = dd.getLatestShortPatch();
        int q = (queueId == null) ? 420 : queueId;
        Integer championKey;
        try {
            championKey = dd.getChampionKey(championId, locale);
        } catch (Exception e) {
            log.error("Failed to resolve champion key for {}: {}", championId, e.toString());
            return;
        }
        if (championKey == null) {
            log.warn("Champion key not found for {}", championId);
            return;
        }

        List<String> tiers = Arrays.asList("EMERALD", "DIAMOND");
        List<String> divisions = Arrays.asList("I","II","III","IV");
        String queueStr = (q == 440) ? "RANKED_FLEX_SR" : "RANKED_SOLO_5x5";

        // Collect summoner IDs (encrypted summonerId) and then resolve to PUUIDs
        List<String> summonerIds = java.util.Collections.synchronizedList(new ArrayList<>());
        java.util.Set<String> seenSummonerIds = java.util.concurrent.ConcurrentHashMap.newKeySet();
        AtomicInteger pages = new AtomicInteger(0);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        BooleanSupplier hasCapacity = () -> {
            synchronized (summonerIds) {
                return summonerIds.size() < maxSummoners;
            }
        };

        outer:
        for (String tier : tiers) {
            for (String div : divisions) {
                for (int page = 1; page <= Math.max(1, pagesToScan); page++) {
                    if (!hasCapacity.getAsBoolean()) {
                        break outer;
                    }
                    int finalPage = page;
                    futures.add(riot.getEntriesByQueueTierDivision(queueStr, tier, div, page)
                            .thenAccept(entries -> {
                                if (entries != null) {
                                    entries.stream()
                                            .limit(200)
                                            .map(le -> le != null ? le.getSummonerId() : null)
                                            .filter(StringUtils::hasText)
                                            .forEach(sid -> {
                                                if (seenSummonerIds.add(sid)) {
                                                    synchronized (summonerIds) {
                                                        if (summonerIds.size() < maxSummoners) {
                                                            summonerIds.add(sid);
                                                        }
                                                    }
                                                }
                                            });
                                }
                                pages.incrementAndGet();
                            })
                            .exceptionally(ex -> { log.warn("Entries fetch failed: {}", ex.toString()); return null; }));
                }
            }
        }
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (CompletionException ex) {
            log.warn("Aggregation {}: failed while collecting league entries: {}", championId, ex.getCause() != null ? ex.getCause().toString() : ex.toString());
        }
        log.info("Aggregation {}: fetched {} summoner ids in {}ms", championId, summonerIds.size(), (System.currentTimeMillis()-t0));

        // Resolve to PUUIDs
        List<String> uniqueSummonerIds;
        synchronized (summonerIds) {
            uniqueSummonerIds = new ArrayList<>(summonerIds);
        }

        List<String> puuids = uniqueSummonerIds.stream()
                .limit(maxSummoners)
                .map(id -> riot.getSummonerById(id)
                        .thenApply(s -> s != null ? s.getPuuid() : null)
                        .exceptionally(ex -> null))
                .map(cf -> {
                    try {
                        return cf.join();
                    } catch (CompletionException ex) {
                        return null;
                    }
                })
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());

        Map<String, Map<Integer, int[]>> itemCounts = new java.util.concurrent.ConcurrentHashMap<>();
        Map<String, Map<String, int[]>> runeCounts = new java.util.concurrent.ConcurrentHashMap<>();
        Map<String, Map<String, int[]>> spellCounts = new java.util.concurrent.ConcurrentHashMap<>();

        // Ensure ALL bucket exists up-front to avoid extra conditionals later
        itemCounts.put("ALL", new java.util.concurrent.ConcurrentHashMap<>());
        runeCounts.put("ALL", new java.util.concurrent.ConcurrentHashMap<>());
        spellCounts.put("ALL", new java.util.concurrent.ConcurrentHashMap<>());

        // Fetch matches per puuid and aggregate
        List<CompletableFuture<Void>> aggFutures = new ArrayList<>();
        for (String puuid : puuids) {
            aggFutures.add(riot.getMatchIdsByPuuid(puuid, matchesPerSummoner)
                    .thenCompose(ids -> {
                        if (ids == null || ids.isEmpty()) return CompletableFuture.completedFuture(Collections.<MatchV5Dto>emptyList());
                        List<CompletableFuture<MatchV5Dto>> mdFuts = ids.stream().map(riot::getMatchDetails).collect(Collectors.toList());
                        return CompletableFuture.allOf(mdFuts.toArray(new CompletableFuture[0]))
                                .thenApply(v -> mdFuts.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toList()));
                    })
                    .thenAccept(matches -> {
                        for (MatchV5Dto m : matches) {
                            try {
                                if (m.getInfo() == null || m.getInfo().getParticipants() == null) continue;
                                if (m.getInfo().getQueueId() != q) continue;
                                String mv = m.getInfo().getGameVersion();
                                if (!StringUtils.hasText(mv) || !mv.startsWith(patch + ".")) continue;
                                long gd = m.getInfo().getGameDuration();
                                if (gd > 0 && gd < 300) continue;
                                for (ParticipantDto p : m.getInfo().getParticipants()) {
                                    if (p == null || p.getChampionId() != championKey.intValue()) continue;
                                    boolean win = p.isWin();
                                    String roleKey = normalizeRole(p.getTeamPosition());

                                    Map<Integer, int[]> allItemsBucket = itemCounts.computeIfAbsent("ALL", key -> new java.util.concurrent.ConcurrentHashMap<>());
                                    int[] slots = {p.getItem0(), p.getItem1(), p.getItem2(), p.getItem3(), p.getItem4(), p.getItem5()};
                                    for (int itemId : slots) {
                                        if (itemId > 0) {
                                            incrementCounter(allItemsBucket, itemId, win);
                                            if (roleKey != null) {
                                                incrementCounter(itemCounts.computeIfAbsent(roleKey, key -> new java.util.concurrent.ConcurrentHashMap<>()), itemId, win);
                                            }
                                        }
                                    }

                                    PerksDto perks = p.getPerks();
                                    if (perks != null && perks.getStyles() != null && perks.getStyles().size() >= 2) {
                                        PerkStyleDto primary = perks.getStyles().get(0);
                                        PerkStyleDto sub = perks.getStyles().get(1);
                                        int keystone = 0;
                                        if (primary != null && primary.getSelections() != null && !primary.getSelections().isEmpty()) {
                                            keystone = primary.getSelections().get(0).getPerk();
                                        }
                                        String runeKey = primary.getStyle() + "|" + sub.getStyle() + "|" + keystone;
                                        Map<String, int[]> allRunesBucket = runeCounts.computeIfAbsent("ALL", key -> new java.util.concurrent.ConcurrentHashMap<>());
                                        incrementCounter(allRunesBucket, runeKey, win);
                                        if (roleKey != null) {
                                            incrementCounter(runeCounts.computeIfAbsent(roleKey, key -> new java.util.concurrent.ConcurrentHashMap<>()), runeKey, win);
                                        }
                                    }

                                    int a = p.getSummoner1Id();
                                    int b = p.getSummoner2Id();
                                    int s1 = Math.min(a, b), s2 = Math.max(a, b);
                                    if (s1 > 0 || s2 > 0) {
                                        String spellKey = s1 + "|" + s2;
                                        Map<String, int[]> allSpellsBucket = spellCounts.computeIfAbsent("ALL", key -> new java.util.concurrent.ConcurrentHashMap<>());
                                        incrementCounter(allSpellsBucket, spellKey, win);
                                        if (roleKey != null) {
                                            incrementCounter(spellCounts.computeIfAbsent(roleKey, key -> new java.util.concurrent.ConcurrentHashMap<>()), spellKey, win);
                                        }
                                    }
                                }
                            } catch (Exception ignore) {
                                // Defensive: aggregation should never fail the entire job because of a single match
                            }
                        }
                    })
                    .exceptionally(ex -> { log.warn("Aggregation match fetch error: {}", ex.toString()); return null; })
            );
        }
        CompletableFuture<Void> aggregateAll = CompletableFuture.allOf(aggFutures.toArray(new CompletableFuture[0]));
        long timeoutMillis = Duration.ofMinutes(5).toMillis();
        try {
            aggregateAll.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            aggFutures.forEach(f -> f.cancel(true));
            log.warn("Aggregation {} timed out after {} ms. Cancelled {} outstanding tasks.", championId, timeoutMillis, aggFutures.size());
            return;
        } catch (InterruptedException e) {
            aggFutures.forEach(f -> f.cancel(true));
            Thread.currentThread().interrupt();
            log.warn("Aggregation {} interrupted: {}", championId, e.toString());
            return;
        } catch (ExecutionException e) {
            aggFutures.forEach(f -> f.cancel(true));
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            log.warn("Aggregation {} encountered an error: {}", championId, cause.toString());
            return;
        }

        // Replace existing aggregates atomically per champion/patch/queue
        List<ChampionItemStat> itemEntities = itemCounts.entrySet().stream()
                .flatMap(roleEntry -> roleEntry.getValue().entrySet().stream()
                        .map(itemEntry -> newItemEntity(championId, roleEntry.getKey(), patch, q, itemEntry.getKey(), itemEntry.getValue())))
                .collect(Collectors.toList());
        List<ChampionRuneStat> runeEntities = runeCounts.entrySet().stream()
                .flatMap(roleEntry -> roleEntry.getValue().entrySet().stream()
                        .map(runeEntry -> newRuneEntity(championId, roleEntry.getKey(), patch, q, runeEntry.getKey(), runeEntry.getValue())))
                .collect(Collectors.toList());
        List<ChampionSpellPairStat> spellEntities = spellCounts.entrySet().stream()
                .flatMap(roleEntry -> roleEntry.getValue().entrySet().stream()
                        .map(spellEntry -> newSpellEntity(championId, roleEntry.getKey(), patch, q, spellEntry.getKey(), spellEntry.getValue())))
                .collect(Collectors.toList());

        transactionTemplate.executeWithoutResult(status -> {
            itemRepo.deleteByChampionIdAndPatchAndQueueId(championId, patch, q);
            runeRepo.deleteByChampionIdAndPatchAndQueueId(championId, patch, q);
            spellRepo.deleteByChampionIdAndPatchAndQueueId(championId, patch, q);

            if (!itemEntities.isEmpty()) {
                itemRepo.saveAll(itemEntities);
            }
            if (!runeEntities.isEmpty()) {
                runeRepo.saveAll(runeEntities);
            }
            if (!spellEntities.isEmpty()) {
                spellRepo.saveAll(spellEntities);
            }
        });

        int distinctItems = itemCounts.getOrDefault("ALL", Collections.emptyMap()).size();
        int distinctRunes = runeCounts.getOrDefault("ALL", Collections.emptyMap()).size();
        int distinctSpells = spellCounts.getOrDefault("ALL", Collections.emptyMap()).size();
        log.info("Aggregation {} done in {}ms. items={}, runes={}, spells={}", championId, (System.currentTimeMillis()-t0), distinctItems, distinctRunes, distinctSpells);
    }

    /**
     * Helper method to increment counter arrays for aggregation statistics.
     *
     * Array format: [totalCount, winCount]
     *
     * @param bucket The map containing counters
     * @param key The key to increment
     * @param win Whether this is a win (increments winCount)
     * @param <K> The key type
     */
    private static <K> void incrementCounter(Map<K, int[]> bucket, K key, boolean win) {
        bucket.compute(key, (k, arr) -> {
            if (arr == null) arr = new int[2];
            arr[0] += 1;
            if (win) arr[1] += 1;
            return arr;
        });
    }

    /**
     * Normalizes raw role strings from match data to standardized values.
     *
     * @param raw The raw role string from match data
     * @return Normalized role (TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY) or null for invalid/NONE
     */
    private String normalizeRole(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String upper = raw.trim().toUpperCase(Locale.ROOT);
        return switch (upper) {
            case "TOP", "JUNGLE", "MIDDLE", "BOTTOM", "UTILITY" -> upper;
            case "MID" -> "MIDDLE";
            case "BOT" -> "BOTTOM";
            case "ADC", "CARRY" -> "BOTTOM";
            case "SUPPORT" -> "UTILITY";
            case "NONE" -> null;
            default -> null;
        };
    }

    /**
     * Creates a new ChampionItemStat entity from aggregated data.
     *
     * @param championId The champion identifier
     * @param role The lane role
     * @param patch The game patch
     * @param queueId The queue type
     * @param itemId The item identifier
     * @param data Array containing [totalCount, winCount]
     * @return New ChampionItemStat entity
     */
    private ChampionItemStat newItemEntity(String championId, String role, String patch, int queueId, int itemId, int[] data) {
        ChampionItemStat entity = new ChampionItemStat();
        entity.setChampionId(championId);
        entity.setRole(role);
        entity.setPatch(patch);
        entity.setQueueId(queueId);
        entity.setItemId(itemId);
        entity.setCount(data[0]);
        entity.setWins(data[1]);
        return entity;
    }

    /**
     * Creates a new ChampionRuneStat entity from aggregated data.
     *
     * @param championId The champion identifier
     * @param role The lane role
     * @param patch The game patch
     * @param queueId The queue type
     * @param key Compound key in format "primaryStyle|subStyle|keystone"
     * @param data Array containing [totalCount, winCount]
     * @return New ChampionRuneStat entity
     */
    private ChampionRuneStat newRuneEntity(String championId, String role, String patch, int queueId, String key, int[] data) {
        String[] parts = key.split("\\|");
        int primary = Integer.parseInt(parts[0]);
        int sub = Integer.parseInt(parts[1]);
        int keystone = Integer.parseInt(parts[2]);

        ChampionRuneStat entity = new ChampionRuneStat();
        entity.setChampionId(championId);
        entity.setRole(role);
        entity.setPatch(patch);
        entity.setQueueId(queueId);
        entity.setPrimaryStyle(primary);
        entity.setSubStyle(sub);
        entity.setKeystone(keystone);
        entity.setCount(data[0]);
        entity.setWins(data[1]);
        return entity;
    }

    /**
     * Creates a new ChampionSpellPairStat entity from aggregated data.
     *
     * @param championId The champion identifier
     * @param role The lane role
     * @param patch The game patch
     * @param queueId The queue type
     * @param key Compound key in format "spell1Id|spell2Id"
     * @param data Array containing [totalCount, winCount]
     * @return New ChampionSpellPairStat entity
     */
    private ChampionSpellPairStat newSpellEntity(String championId, String role, String patch, int queueId, String key, int[] data) {
        String[] parts = key.split("\\|");
        int s1 = Integer.parseInt(parts[0]);
        int s2 = Integer.parseInt(parts[1]);

        ChampionSpellPairStat entity = new ChampionSpellPairStat();
        entity.setChampionId(championId);
        entity.setRole(role);
        entity.setPatch(patch);
        entity.setQueueId(queueId);
        entity.setSpell1Id(s1);
        entity.setSpell2Id(s2);
        entity.setCount(data[0]);
        entity.setWins(data[1]);
        return entity;
    }
}
