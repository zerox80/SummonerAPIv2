package com.zerox80.riotapi.service;

import com.zerox80.riotapi.client.RiotApiClient;
import com.zerox80.riotapi.dto.ChampionBuildDto;
import com.zerox80.riotapi.dto.ItemStatDto;
import com.zerox80.riotapi.dto.RuneStatDto;
import com.zerox80.riotapi.dto.SpellPairStatDto;
import com.zerox80.riotapi.model.*;
import com.zerox80.riotapi.repository.ChampionItemStatRepository;
import com.zerox80.riotapi.repository.ChampionRuneStatRepository;
import com.zerox80.riotapi.repository.ChampionSpellPairStatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.scheduling.annotation.Async;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class BuildAggregationService {

    private static final Logger log = LoggerFactory.getLogger(BuildAggregationService.class);

    private final RiotApiClient riot;
    private final DataDragonService dd;
    private final ChampionItemStatRepository itemRepo;
    private final ChampionRuneStatRepository runeRepo;
    private final ChampionSpellPairStatRepository spellRepo;

    public BuildAggregationService(RiotApiClient riot,
                                   DataDragonService dd,
                                   ChampionItemStatRepository itemRepo,
                                   ChampionRuneStatRepository runeRepo,
                                   ChampionSpellPairStatRepository spellRepo) {
        this.riot = riot;
        this.dd = dd;
        this.itemRepo = itemRepo;
        this.runeRepo = runeRepo;
        this.spellRepo = spellRepo;
    }

    public ChampionBuildDto loadBuild(String championId, Integer queueId, String role, Locale locale) {
        String patch = dd.getLatestShortPatch();
        int q = (queueId == null) ? 420 : queueId; // default SoloQ
        String roleUse = (StringUtils.hasText(role) ? role.toUpperCase(Locale.ROOT) : null);

        // Items (top 10)
        List<ChampionItemStat> items = (roleUse != null && !roleUse.equals("ALL"))
                ? itemRepo.findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(championId, roleUse, patch, q)
                : itemRepo.findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(championId, patch, q);

        // Runes (top 10)
        List<ChampionRuneStat> runes = (roleUse != null && !roleUse.equals("ALL"))
                ? runeRepo.findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(championId, roleUse, patch, q)
                : runeRepo.findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(championId, patch, q);

        // Spells (top 10)
        List<ChampionSpellPairStat> spells = (roleUse != null && !roleUse.equals("ALL"))
                ? spellRepo.findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(championId, roleUse, patch, q)
                : spellRepo.findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(championId, patch, q);

        Map<Integer, ItemSummary> itemLookup;
        Map<Integer, SummonerSpellInfo> spellLookup;
        Map<Integer, RunePerkInfo> runePerkLookup;
        Map<Integer, String> styleNames;
        try {
            itemLookup = dd.getItems(locale);
            spellLookup = dd.getSummonerSpells(locale);
            runePerkLookup = dd.getRunePerkLookup(locale);
            styleNames = dd.getRuneStyleNames(locale);
        } catch (Exception e) {
            log.warn("DDragon lookups failed: {}", e.toString());
            itemLookup = Collections.emptyMap();
            spellLookup = Collections.emptyMap();
            runePerkLookup = Collections.emptyMap();
            styleNames = Collections.emptyMap();
        }
        Map<String,String> bases = dd.getImageBases(null);

        List<ItemStatDto> itemDtos = items.stream().map(s -> {
            ItemSummary info = itemLookup.get(s.getItemId());
            String name = info != null ? info.getName() : ("Item " + s.getItemId());
            String icon = info != null ? bases.get("item") + info.getImageFull() : null;
            return new ItemStatDto(s.getItemId(), s.getCount(), s.getWins(), name, icon);
        }).collect(Collectors.toList());

        List<RuneStatDto> runeDtos = runes.stream().map(s -> {
            String pName = styleNames.getOrDefault(s.getPrimaryStyle(), String.valueOf(s.getPrimaryStyle()));
            String sName = styleNames.getOrDefault(s.getSubStyle(), String.valueOf(s.getSubStyle()));
            RunePerkInfo k = runePerkLookup.get(s.getKeystone());
            String kName = k != null ? k.getName() : String.valueOf(s.getKeystone());
            String kIcon = k != null ? ("https://ddragon.leagueoflegends.com/cdn/img/" + k.getIconFull()) : null;
            return new RuneStatDto(s.getPrimaryStyle(), s.getSubStyle(), s.getKeystone(), s.getCount(), s.getWins(), pName, sName, kName, kIcon);
        }).collect(Collectors.toList());

        List<SpellPairStatDto> spellDtos = spells.stream().map(s -> {
            SummonerSpellInfo s1 = spellLookup.get(s.getSpell1Id());
            SummonerSpellInfo s2 = spellLookup.get(s.getSpell2Id());
            String s1Name = s1 != null ? s1.getName() : String.valueOf(s.getSpell1Id());
            String s2Name = s2 != null ? s2.getName() : String.valueOf(s.getSpell2Id());
            String s1Icon = s1 != null ? bases.get("spell") + s1.getImageFull() : null;
            String s2Icon = s2 != null ? bases.get("spell") + s2.getImageFull() : null;
            return new SpellPairStatDto(s.getSpell1Id(), s.getSpell2Id(), s.getCount(), s.getWins(), s1Name, s2Name, s1Icon, s2Icon);
        }).collect(Collectors.toList());

        return new ChampionBuildDto(championId, patch, q, roleUse, itemDtos, runeDtos, spellDtos);
    }

    @Async("appTaskExecutor")
    @Transactional
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
        List<String> summonerIds = new CopyOnWriteArrayList<>();
        AtomicInteger pages = new AtomicInteger(0);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        outer:
        for (String tier : tiers) {
            for (String div : divisions) {
                for (int page = 1; page <= Math.max(1, pagesToScan); page++) {
                    int finalPage = page;
                    futures.add(riot.getEntriesByQueueTierDivision(queueStr, tier, div, page)
                            .thenAccept(entries -> {
                                if (entries != null) {
                                    entries.stream().limit(200).forEach(le -> {
                                        if (summonerIds.size() < maxSummoners) {
                                            summonerIds.add(le.getSummonerId());
                                        }
                                    });
                                }
                                pages.incrementAndGet();
                            })
                            .exceptionally(ex -> { log.warn("Entries fetch failed: {}", ex.toString()); return null; }));
                    if (summonerIds.size() >= maxSummoners) break outer;
                }
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("Aggregation {}: fetched {} summoner ids in {}ms", championId, summonerIds.size(), (System.currentTimeMillis()-t0));

        // Resolve to PUUIDs
        List<String> puuids = summonerIds.stream().limit(maxSummoners).map(id -> riot.getSummonerById(id)
                .thenApply(s -> s != null ? s.getPuuid() : null)
                .exceptionally(ex -> null))
            .collect(Collectors.toList())
            .stream().map(CompletableFuture::join)
            .filter(StringUtils::hasText)
            .collect(Collectors.toList());

        // Prepare counters
        Map<Integer, int[]> itemCounts = new HashMap<>(); // id -> [count, wins]
        Map<String, int[]> runeCounts = new HashMap<>(); // key: primary|sub|keystone
        Map<String, int[]> spellCounts = new HashMap<>(); // key: min(sp1,sp2)|max(...)

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
                                for (ParticipantDto p : m.getInfo().getParticipants()) {
                                    if (p == null) continue;
                                    if (p.getChampionId() == championKey.intValue()) {
                                        boolean win = p.isWin();
                                        // items
                                        int[] slots = {p.getItem0(), p.getItem1(), p.getItem2(), p.getItem3(), p.getItem4(), p.getItem5()};
                                        for (int itemId : slots) {
                                            if (itemId <= 0) continue;
                                            int[] arr = itemCounts.computeIfAbsent(itemId, k -> new int[2]);
                                            arr[0] += 1; if (win) arr[1] += 1;
                                        }
                                        // runes
                                        PerksDto perks = p.getPerks();
                                        if (perks != null && perks.getStyles() != null && perks.getStyles().size() >= 2) {
                                            PerkStyleDto primary = perks.getStyles().get(0);
                                            PerkStyleDto sub = perks.getStyles().get(1);
                                            int keystone = 0;
                                            if (primary.getSelections() != null && !primary.getSelections().isEmpty()) {
                                                keystone = primary.getSelections().get(0).getPerk();
                                            }
                                            String key = primary.getStyle() + "|" + sub.getStyle() + "|" + keystone;
                                            int[] arr = runeCounts.computeIfAbsent(key, k -> new int[2]);
                                            arr[0] += 1; if (win) arr[1] += 1;
                                        }
                                        // summoner spells
                                        int a = p.getSummoner1Id();
                                        int b = p.getSummoner2Id();
                                        int s1 = Math.min(a, b), s2 = Math.max(a, b);
                                        String key = s1 + "|" + s2;
                                        int[] arr = spellCounts.computeIfAbsent(key, k -> new int[2]);
                                        arr[0] += 1; if (win) arr[1] += 1;
                                    }
                                }
                            } catch (Exception ignore) {}
                        }
                    })
                    .exceptionally(ex -> { log.warn("Aggregation match fetch error: {}", ex.toString()); return null; })
            );
        }
        CompletableFuture.allOf(aggFutures.toArray(new CompletableFuture[0])).orTimeout(Duration.ofMinutes(5).toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS).join();

        // Flush to DB
        itemCounts.forEach((itemId, arr) -> itemRepo.findByChampionIdAndRoleAndPatchAndQueueIdAndItemId(championId, "ALL", patch, q, itemId)
                .map(e -> { e.setCount(e.getCount()+arr[0]); e.setWins(e.getWins()+arr[1]); return itemRepo.save(e); })
                .orElseGet(() -> itemRepo.save(newEntity(championId, patch, q, itemId, arr[0], arr[1]))));

        runeCounts.forEach((k, arr) -> {
            String[] parts = k.split("\\|");
            int primary = Integer.parseInt(parts[0]);
            int sub = Integer.parseInt(parts[1]);
            int keystone = Integer.parseInt(parts[2]);
            runeRepo.findByChampionIdAndRoleAndPatchAndQueueIdAndPrimaryStyleAndSubStyleAndKeystone(championId, "ALL", patch, q, primary, sub, keystone)
                    .map(e -> { e.setCount(e.getCount()+arr[0]); e.setWins(e.getWins()+arr[1]); return runeRepo.save(e); })
                    .orElseGet(() -> {
                        ChampionRuneStat e = new ChampionRuneStat();
                        e.setChampionId(championId); e.setRole("ALL"); e.setPatch(patch); e.setQueueId(q);
                        e.setPrimaryStyle(primary); e.setSubStyle(sub); e.setKeystone(keystone);
                        e.setCount(arr[0]); e.setWins(arr[1]);
                        return runeRepo.save(e);
                    });
        });

        spellCounts.forEach((k, arr) -> {
            String[] parts = k.split("\\|");
            int s1 = Integer.parseInt(parts[0]);
            int s2 = Integer.parseInt(parts[1]);
            spellRepo.findByChampionIdAndRoleAndPatchAndQueueIdAndSpell1IdAndSpell2Id(championId, "ALL", patch, q, s1, s2)
                    .map(e -> { e.setCount(e.getCount()+arr[0]); e.setWins(e.getWins()+arr[1]); return spellRepo.save(e); })
                    .orElseGet(() -> {
                        ChampionSpellPairStat e = new ChampionSpellPairStat();
                        e.setChampionId(championId); e.setRole("ALL"); e.setPatch(patch); e.setQueueId(q);
                        e.setSpell1Id(s1); e.setSpell2Id(s2); e.setCount(arr[0]); e.setWins(arr[1]);
                        return spellRepo.save(e);
                    });
        });

        log.info("Aggregation {} done in {}ms. items={}, runes={}, spells={}", championId, (System.currentTimeMillis()-t0), itemCounts.size(), runeCounts.size(), spellCounts.size());
    }

    private ChampionItemStat newEntity(String championId, String patch, int q, int itemId, int count, int wins) {
        ChampionItemStat e = new ChampionItemStat();
        e.setChampionId(championId);
        e.setRole("ALL");
        e.setPatch(patch);
        e.setQueueId(q);
        e.setItemId(itemId);
        e.setCount(count);
        e.setWins(wins);
        return e;
    }
}
