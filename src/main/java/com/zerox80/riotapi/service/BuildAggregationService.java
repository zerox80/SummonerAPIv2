package com.zerox80.riotapi.service; // Paketdeklaration für den Service-Layer

import com.zerox80.riotapi.client.RiotApiClient; // Import des Riot API Clients für externe API-Aufrufe
import com.zerox80.riotapi.dto.ChampionBuildDto; // Import für Champion-Build-Response-DTO
import com.zerox80.riotapi.dto.ItemStatDto; // Import für Item-Statistik-DTO
import com.zerox80.riotapi.dto.RuneStatDto; // Import für Runen-Statistik-DTO
import com.zerox80.riotapi.dto.SpellPairStatDto; // Import für Summoner-Spell-Paar-Statistik-DTO
import com.zerox80.riotapi.model.*; // Import aller Model-Klassen (DTOs und Entities)
import com.zerox80.riotapi.repository.ChampionItemStatRepository; // Import für Item-Statistik-Repository (Database)
import com.zerox80.riotapi.repository.ChampionRuneStatRepository; // Import für Runen-Statistik-Repository (Database)
import com.zerox80.riotapi.repository.ChampionSpellPairStatRepository; // Import für Spell-Statistik-Repository (Database)
import org.slf4j.Logger; // Import für Logging-Schnittstelle
import org.slf4j.LoggerFactory; // Import für Logger-Factory zur Logger-Instanziierung
import org.springframework.scheduling.annotation.Async; // Import für asynchrone Methoden-Ausführung
import org.springframework.stereotype.Service; // Import für Service-Komponenten-Annotation
import org.springframework.transaction.PlatformTransactionManager; // Import für programmatische Transaktionsverwaltung
import org.springframework.transaction.support.TransactionTemplate; // Import für transaktionale Code-Blöcke
import org.springframework.util.StringUtils; // Import für String-Utility-Funktionen

import java.time.Duration; // Import für Zeitdauer-Berechnungen
import java.util.*; // Import aller Java-Utility-Klassen
import java.util.concurrent.CompletableFuture; // Import für asynchrone/nicht-blockierende Operationen
import java.util.concurrent.CompletionException; // Import für Exception-Handling in CompletableFutures
import java.util.concurrent.atomic.AtomicInteger; // Import für thread-sichere Integer-Operationen
import java.util.function.BooleanSupplier; // Import für Supplier-Functional-Interface
import java.util.stream.Collectors; // Import für Stream-Sammler
import java.util.concurrent.ExecutionException; // Import für Future-Exception-Handling
import java.util.concurrent.TimeUnit; // Import für Zeiteinheiten
import java.util.concurrent.TimeoutException; // Import für Timeout-Exceptions


@Service // Markiert diese Klasse als Spring Service-Komponente für Dependency Injection
public class BuildAggregationService { // Service-Klasse für Champion-Build-Daten-Aggregation aus hochrangigen Spieler-Matches

    private static final Logger log = LoggerFactory.getLogger(BuildAggregationService.class); // Logger-Instanz für Logging in diesem Service

    private final RiotApiClient riot; // Client für HTTP-Aufrufe an die Riot API
    private final DataDragonService dd; // Service für statische Daten (Champion-Keys, Item-Namen, etc.)
    private final ChampionItemStatRepository itemRepo; // Database-Repository für Item-Build-Statistiken
    private final ChampionRuneStatRepository runeRepo; // Database-Repository für Runen-Statistiken
    private final ChampionSpellPairStatRepository spellRepo; // Database-Repository für Summoner-Spell-Statistiken
    private final TransactionTemplate transactionTemplate; // Template für programmatische Transaktionsverwaltung


    public BuildAggregationService(RiotApiClient riot, // Konstruktor mit Dependency Injection aller benötigten Services/Repositories
                                   DataDragonService dd, // Injiziert DataDragon-Service
                                   ChampionItemStatRepository itemRepo, // Injiziert Item-Repository
                                   ChampionRuneStatRepository runeRepo, // Injiziert Runen-Repository
                                   ChampionSpellPairStatRepository spellRepo, // Injiziert Spell-Repository
                                   PlatformTransactionManager transactionManager) { // Injiziert Transaction-Manager für DB-Transaktionen
        this.riot = riot; // Weist injizierten Client der Instanzvariable zu
        this.dd = dd; // Weist injizierten Service der Instanzvariable zu
        this.itemRepo = itemRepo; // Weist injiziertes Repository der Instanzvariable zu
        this.runeRepo = runeRepo; // Weist injiziertes Repository der Instanzvariable zu
        this.spellRepo = spellRepo; // Weist injiziertes Repository der Instanzvariable zu
        this.transactionTemplate = new TransactionTemplate(transactionManager); // Erstellt Transaction-Template für transaktionale Code-Blöcke
    }

    private List<ChampionItemStat> fetchItems(String championId, String patch, int queueId, String role) { // Private Hilfsmethode zum Abrufen der Top-10-Item-Statistiken aus Database
        if (StringUtils.hasText(role) && !"ALL".equals(role)) { // Prüft ob spezifische Rolle angegeben (z.B. "TOP", "JUNGLE")
            return itemRepo.findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(championId, role, patch, queueId); // Database-Query: Holt Top 10 Items für Champion+Rolle+Patch+Queue sortiert nach Häufigkeit
        }
        return itemRepo.findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(championId, patch, queueId); // Database-Query: Holt Top 10 Items für Champion+Patch+Queue (alle Rollen aggregiert)
    }

    private List<ChampionRuneStat> fetchRunes(String championId, String patch, int queueId, String role) { // Private Hilfsmethode zum Abrufen der Top-10-Runen-Statistiken aus Database
        if (StringUtils.hasText(role) && !"ALL".equals(role)) { // Prüft ob spezifische Rolle angegeben
            return runeRepo.findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(championId, role, patch, queueId); // Database-Query: Holt Top 10 Runen-Kombinationen für Champion+Rolle+Patch+Queue
        }
        return runeRepo.findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(championId, patch, queueId); // Database-Query: Holt Top 10 Runen-Kombinationen für Champion+Patch+Queue (alle Rollen)
    }

    private List<ChampionSpellPairStat> fetchSpells(String championId, String patch, int queueId, String role) { // Private Hilfsmethode zum Abrufen der Top-10-Summoner-Spell-Statistiken aus Database
        if (StringUtils.hasText(role) && !"ALL".equals(role)) { // Prüft ob spezifische Rolle angegeben
            return spellRepo.findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(championId, role, patch, queueId); // Database-Query: Holt Top 10 Summoner-Spell-Paare für Champion+Rolle+Patch+Queue
        }
        return spellRepo.findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(championId, patch, queueId); // Database-Query: Holt Top 10 Summoner-Spell-Paare für Champion+Patch+Queue (alle Rollen)
    }


    public ChampionBuildDto loadBuild(String championId, Integer queueId, String role, Locale locale) { // Öffentliche Methode zum Laden aggregierter Champion-Build-Daten aus Database
        String patch = dd.getLatestShortPatch(); // Holt aktuellen Patch (z.B. "15.18")
        int q = (queueId == null) ? 420 : queueId; // default SoloQ
        String requestedRole = (StringUtils.hasText(role) ? role.toUpperCase(Locale.ROOT) : "ALL"); // Normalisiert Rolle zu Großbuchstaben oder "ALL"

        List<ChampionItemStat> items = fetchItems(championId, patch, q, requestedRole); // Database-Query: Holt Item-Statistiken für angeforderte Rolle
        List<ChampionRuneStat> runes = fetchRunes(championId, patch, q, requestedRole); // Database-Query: Holt Runen-Statistiken für angeforderte Rolle
        List<ChampionSpellPairStat> spells = fetchSpells(championId, patch, q, requestedRole); // Database-Query: Holt Spell-Statistiken für angeforderte Rolle

        String effectiveRole = requestedRole; // Initialisiert effektive Rolle mit angeforderter Rolle
        if (!"ALL".equals(requestedRole) && items.isEmpty() && runes.isEmpty() && spells.isEmpty()) { // Business-Logik: Prüft ob keine Daten für spezifische Rolle vorhanden
            effectiveRole = "ALL"; // Fallback: Verwendet aggregierte Daten über alle Rollen
            items = fetchItems(championId, patch, q, effectiveRole); // Database-Query: Holt Item-Statistiken für alle Rollen
            runes = fetchRunes(championId, patch, q, effectiveRole); // Database-Query: Holt Runen-Statistiken für alle Rollen
            spells = fetchSpells(championId, patch, q, effectiveRole); // Database-Query: Holt Spell-Statistiken für alle Rollen
        }

        Map<Integer, ItemSummary> itemLookupTmp; // Temporäre Variable für Item-Lookup-Map (für final-Zuweisung)
        Map<Integer, SummonerSpellInfo> spellLookupTmp; // Temporäre Variable für Spell-Lookup-Map
        Map<Integer, RunePerkInfo> runePerkLookupTmp; // Temporäre Variable für Runen-Perk-Lookup-Map
        Map<Integer, String> styleNamesTmp; // Temporäre Variable für Runen-Style-Namen-Map
        try { // Try-Block für fehlertolerante DataDragon-Abfragen
            itemLookupTmp = dd.getItems(locale); // Holt Item-Daten von DataDragon (Namen, Icons)
            spellLookupTmp = dd.getSummonerSpells(locale); // Holt Summoner-Spell-Daten von DataDragon
            runePerkLookupTmp = dd.getRunePerkLookup(locale); // Holt Runen-Perk-Daten von DataDragon
            styleNamesTmp = dd.getRuneStyleNames(locale); // Holt Runen-Style-Namen von DataDragon
        } catch (Exception e) { // Fängt DataDragon-Fehler ab
            log.warn("DDragon lookups failed: {}", e.toString()); // Warnung bei DataDragon-Fehler
            itemLookupTmp = Collections.emptyMap(); // Fallback: Leere Map für Items
            spellLookupTmp = Collections.emptyMap(); // Fallback: Leere Map für Spells
            runePerkLookupTmp = Collections.emptyMap(); // Fallback: Leere Map für Runen-Perks
            styleNamesTmp = Collections.emptyMap(); // Fallback: Leere Map für Style-Namen
        }
        final Map<Integer, ItemSummary> itemLookup = itemLookupTmp; // Final-Zuweisung für Lambda-Verwendung
        final Map<Integer, SummonerSpellInfo> spellLookup = spellLookupTmp; // Final-Zuweisung für Lambda-Verwendung
        final Map<Integer, RunePerkInfo> runePerkLookup = runePerkLookupTmp; // Final-Zuweisung für Lambda-Verwendung
        final Map<Integer, String> styleNames = styleNamesTmp; // Final-Zuweisung für Lambda-Verwendung
        final Map<String,String> bases = dd.getImageBases(null); // Holt Basis-URLs für Bilder von DataDragon

        List<ItemStatDto> itemDtos = items.stream().map(s -> { // Business-Logik: Transformiert Item-Statistik-Entities zu DTOs mit angereicherten Daten
            ItemSummary info = itemLookup.get(s.getItemId()); // Holt Item-Info aus Lookup-Map
            String name = info != null ? info.getName() : ("Item " + s.getItemId()); // Verwendet Item-Name oder Fallback "Item {ID}"
            String icon = info != null ? bases.get("item") + info.getImageFull() : null; // Generiert Icon-URL
            return new ItemStatDto(s.getItemId(), s.getCount(), s.getWins(), name, icon); // Erstellt DTO mit Statistiken und Metadaten
        }).collect(Collectors.toList()); // Sammelt in Liste

        List<RuneStatDto> runeDtos = runes.stream().map(s -> { // Business-Logik: Transformiert Runen-Statistik-Entities zu DTOs mit angereicherten Daten
            String pName = styleNames.getOrDefault(s.getPrimaryStyle(), String.valueOf(s.getPrimaryStyle())); // Holt Primary-Style-Name oder Fallback ID
            String sName = styleNames.getOrDefault(s.getSubStyle(), String.valueOf(s.getSubStyle())); // Holt Sub-Style-Name oder Fallback ID
            RunePerkInfo k = runePerkLookup.get(s.getKeystone()); // Holt Keystone-Info aus Lookup-Map
            String kName = k != null ? k.getName() : String.valueOf(s.getKeystone()); // Verwendet Keystone-Name oder Fallback ID
            String kIcon = k != null ? ("https://ddragon.leagueoflegends.com/cdn/img/" + k.getIconFull()) : null; // Generiert Keystone-Icon-URL
            return new RuneStatDto(s.getPrimaryStyle(), s.getSubStyle(), s.getKeystone(), s.getCount(), s.getWins(), pName, sName, kName, kIcon); // Erstellt DTO mit Statistiken und Metadaten
        }).collect(Collectors.toList()); // Sammelt in Liste

        List<SpellPairStatDto> spellDtos = spells.stream().map(s -> { // Business-Logik: Transformiert Spell-Statistik-Entities zu DTOs mit angereicherten Daten
            SummonerSpellInfo s1 = spellLookup.get(s.getSpell1Id()); // Holt Info für Spell 1 aus Lookup-Map
            SummonerSpellInfo s2 = spellLookup.get(s.getSpell2Id()); // Holt Info für Spell 2 aus Lookup-Map
            String s1Name = s1 != null ? s1.getName() : String.valueOf(s.getSpell1Id()); // Verwendet Spell-1-Name oder Fallback ID
            String s2Name = s2 != null ? s2.getName() : String.valueOf(s.getSpell2Id()); // Verwendet Spell-2-Name oder Fallback ID
            String s1Icon = s1 != null ? bases.get("spell") + s1.getImageFull() : null; // Generiert Spell-1-Icon-URL
            String s2Icon = s2 != null ? bases.get("spell") + s2.getImageFull() : null; // Generiert Spell-2-Icon-URL
            return new SpellPairStatDto(s.getSpell1Id(), s.getSpell2Id(), s.getCount(), s.getWins(), s1Name, s2Name, s1Icon, s2Icon); // Erstellt DTO mit Statistiken und Metadaten
        }).collect(Collectors.toList()); // Sammelt in Liste

        return new ChampionBuildDto(championId, patch, q, effectiveRole, itemDtos, runeDtos, spellDtos); // Erstellt finales Champion-Build-DTO mit allen angereicherten Statistiken
    }

    
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

    private static <K> void incrementCounter(Map<K, int[]> bucket, K key, boolean win) {
        bucket.compute(key, (k, arr) -> {
            if (arr == null) arr = new int[2];
            arr[0] += 1;
            if (win) arr[1] += 1;
            return arr;
        });
    }

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
