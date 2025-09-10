package com.zerox80.riotapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerox80.riotapi.model.ChampionDetail;
import com.zerox80.riotapi.model.ChampionSummary;
import com.zerox80.riotapi.model.PassiveSummary;
import com.zerox80.riotapi.model.SpellSummary;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataDragonService {

    private static final String DDRAGON_BASE = "https://ddragon.leagueoflegends.com";
    private static final String DEFAULT_LOCALE = "en_US";

    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public DataDragonService(HttpClient riotApiHttpClient) {
        this.httpClient = riotApiHttpClient;
    }

    private String sanitizeChampionIdBasic(String raw) {
        if (raw == null) return null;
        String cleaned = raw.replaceAll("[^A-Za-z]", "");
        if (cleaned.length() > 30) {
            cleaned = cleaned.substring(0, 30);
        }
        return cleaned.isBlank() ? null : cleaned;
    }

    @Cacheable(cacheNames = "ddragonVersions")
    public List<String> getAllVersions() {
        String url = DDRAGON_BASE + "/api/versions.json";
        try {
            JsonNode node = getJson(url);
            if (node.isArray()) {
                List<String> versions = new ArrayList<>();
                node.forEach(n -> versions.add(n.asText()));
                return versions;
            }
        } catch (Exception e) {
            // ignore and fall through
        }
        return Collections.emptyList();
    }

    public String getLatestVersion() {
        List<String> versions = getAllVersions();
        return versions.isEmpty() ? "latest" : versions.get(0);
    }

    public String getLatestShortPatch() {
        String v = getLatestVersion();
        // e.g., 15.18.1 -> 15.18
        String[] parts = v.split("\\.");
        if (parts.length >= 2) return parts[0] + "." + parts[1];
        return v;
    }

    public String resolveLocale(Locale locale) {
        if (locale == null) return DEFAULT_LOCALE;
        String lang = locale.toLanguageTag();
        // Map common language tags to DDragon format
        if (lang.startsWith("de")) return "de_DE";
        if (lang.startsWith("en")) return "en_US";
        if (lang.startsWith("fr")) return "fr_FR";
        if (lang.startsWith("es")) return "es_ES";
        if (lang.startsWith("pt")) return "pt_BR";
        if (lang.startsWith("pl")) return "pl_PL";
        if (lang.startsWith("tr")) return "tr_TR";
        if (lang.startsWith("ru")) return "ru_RU";
        if (lang.startsWith("it")) return "it_IT";
        if (lang.startsWith("ja")) return "ja_JP";
        if (lang.startsWith("ko")) return "ko_KR";
        if (lang.startsWith("zh")) return "zh_CN"; // Simplified Chinese
        return DEFAULT_LOCALE;
    }

    @Cacheable(cacheNames = "ddragonChampionList", key = "#root.target.getLatestVersion() + '|' + #root.target.resolveLocale(#locale)")
    public List<ChampionSummary> getChampionSummaries(Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        String url = DDRAGON_BASE + "/cdn/" + ver + "/data/" + loc + "/champion.json";
        JsonNode root = getJson(url);
        JsonNode data = root.path("data");
        if (data.isMissingNode() || !data.isObject()) return Collections.emptyList();
        List<ChampionSummary> list = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> fields = data.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode c = entry.getValue();
            String id = c.path("id").asText("");
            String name = c.path("name").asText("");
            String title = c.path("title").asText("");
            List<String> tags = new ArrayList<>();
            if (c.path("tags").isArray()) {
                c.path("tags").forEach(t -> tags.add(t.asText()));
            }
            String imageFull = c.path("image").path("full").asText("");
            list.add(new ChampionSummary(id, name, title, tags, imageFull));
        }
        // Sort by name
        return list.stream().sorted((a,b) -> a.getName().compareToIgnoreCase(b.getName())).collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "ddragonChampionDetail", key = "#championId + '|' + #root.target.getLatestVersion() + '|' + #root.target.resolveLocale(#locale)")
    public ChampionDetail getChampionDetail(String championId, Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        String cid = sanitizeChampionIdBasic(championId);
        if (cid == null) return null;
        String url = DDRAGON_BASE + "/cdn/" + ver + "/data/" + loc + "/champion/" + cid + ".json";
        JsonNode root = getJson(url);
        JsonNode data = root.path("data").path(cid);
        if (data.isMissingNode()) {
            // Try normalized ID (capitalize first letter)
            String norm = normalizeChampionId(cid);
            data = root.path("data").path(norm);
            if (data.isMissingNode()) return null;
            cid = norm;
        }
        String id = data.path("id").asText(cid);
        String name = data.path("name").asText("");
        String title = data.path("title").asText("");
        String lore = data.path("lore").asText("");
        List<String> tags = new ArrayList<>();
        if (data.path("tags").isArray()) data.path("tags").forEach(t -> tags.add(t.asText()));
        String imageFull = data.path("image").path("full").asText("");

        // Passive
        PassiveSummary passive = null;
        JsonNode p = data.path("passive");
        if (p != null && !p.isMissingNode()) {
            String pName = p.path("name").asText("");
            String pDesc = p.path("description").asText("");
            String pImg = p.path("image").path("full").asText("");
            passive = new PassiveSummary(pName, pDesc, pImg);
        }

        // Spells
        List<SpellSummary> spells = new ArrayList<>();
        JsonNode spellsNode = data.path("spells");
        if (spellsNode != null && spellsNode.isArray()) {
            for (JsonNode s : spellsNode) {
                String sid = s.path("id").asText("");
                String sname = s.path("name").asText("");
                String tooltip = s.path("tooltip").asText("");
                String sImg = s.path("image").path("full").asText("");
                spells.add(new SpellSummary(sid, sname, tooltip, sImg));
            }
        }
        return new ChampionDetail(id, name, title, lore, tags, imageFull, passive, spells);
    }

    /**
     * Returns the numeric champion key for a given DDragon champion id (e.g., Anivia -> 34).
     */
    @Cacheable(cacheNames = "ddragonChampionDetail", key = "'key-' + #championId + '|' + #root.target.getLatestVersion() + '|' + #root.target.resolveLocale(#locale)")
    public Integer getChampionKey(String championId, Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        String cid = sanitizeChampionIdBasic(championId);
        if (cid == null) return null;
        String url = DDRAGON_BASE + "/cdn/" + ver + "/data/" + loc + "/champion/" + cid + ".json";
        JsonNode root = getJson(url);
        JsonNode data = root.path("data").path(cid);
        if (data.isMissingNode()) {
            String norm = normalizeChampionId(cid);
            data = root.path("data").path(norm);
            if (data.isMissingNode()) return null;
        }
        String keyStr = data.path("key").asText(null);
        if (keyStr == null) return null;
        try {
            return Integer.parseInt(keyStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalizeChampionId(String raw) {
        if (raw == null || raw.isBlank()) return raw;
        String r = raw.trim();
        return r.substring(0,1).toUpperCase(Locale.ROOT) + r.substring(1);
    }

    private JsonNode getJson(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .header("Accept", "application/json")
                .header("User-Agent", "SummonerAPI/1.0 (+https://stats.rujbin.eu)")
                .build();

        try {
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() / 100 == 2) {
                return mapper.readTree(res.body());
            }
        } catch (IOException e) {
            // fall through to fallback client
        }

        // Fallback: retry once with a simple HTTP/1.1 client (helps in rare ALPN/HTTP2 issues)
        HttpClient fallback = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpResponse<String> res2 = fallback.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res2.statusCode() / 100 != 2) {
            throw new IOException("HTTP " + res2.statusCode() + " from " + url);
        }
        return mapper.readTree(res2.body());
    }

    @Cacheable(cacheNames = "ddragonImageBases", key = "#version == null || #version.isBlank() ? 'latest' : #version")
    public Map<String, String> getImageBases(String version) {
        String ver = (version == null || version.isBlank()) ? getLatestVersion() : version;
        Map<String,String> map = new LinkedHashMap<>();
        map.put("version", ver);
        map.put("cdn", DDRAGON_BASE + "/cdn/");
        map.put("img", DDRAGON_BASE + "/cdn/" + ver + "/img/");
        map.put("champSquare", DDRAGON_BASE + "/cdn/" + ver + "/img/champion/");
        map.put("item", DDRAGON_BASE + "/cdn/" + ver + "/img/item/");
        map.put("spell", DDRAGON_BASE + "/cdn/" + ver + "/img/spell/");
        map.put("passive", DDRAGON_BASE + "/cdn/" + ver + "/img/passive/");
        map.put("splash", DDRAGON_BASE + "/cdn/img/champion/splash/");
        return map;
    }

    // ===== Items =====
    @Cacheable(cacheNames = "ddragonItems", key = "#root.target.getLatestVersion() + '|' + #root.target.resolveLocale(#locale)")
    public Map<Integer, com.zerox80.riotapi.model.ItemSummary> getItems(Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        String url = DDRAGON_BASE + "/cdn/" + ver + "/data/" + loc + "/item.json";
        JsonNode root = getJson(url);
        JsonNode data = root.path("data");
        if (!data.isObject()) return Collections.emptyMap();
        Map<Integer, com.zerox80.riotapi.model.ItemSummary> map = new java.util.HashMap<>();
        data.fields().forEachRemaining(e -> {
            int id = Integer.parseInt(e.getKey());
            JsonNode n = e.getValue();
            String name = n.path("name").asText("");
            String imageFull = n.path("image").path("full").asText("");
            map.put(id, new com.zerox80.riotapi.model.ItemSummary(id, name, imageFull));
        });
        return map;
    }

    // ===== Runes (Reforged) =====
    @Cacheable(cacheNames = "ddragonRunes", key = "#root.target.getLatestVersion() + '|' + #root.target.resolveLocale(#locale)")
    public Map<Integer, String> getRuneStyleNames(Locale locale) throws IOException, InterruptedException {
        Map<Integer, String> styleNames = new java.util.HashMap<>();
        for (JsonNode style : getRunesArray(locale)) {
            int id = style.path("id").asInt();
            String name = style.path("name").asText("");
            styleNames.put(id, name);
        }
        return styleNames;
    }

    @Cacheable(cacheNames = "ddragonRunes", key = "'perks-' + #root.target.getLatestVersion() + '|' + #root.target.resolveLocale(#locale)")
    public Map<Integer, com.zerox80.riotapi.model.RunePerkInfo> getRunePerkLookup(Locale locale) throws IOException, InterruptedException {
        Map<Integer, com.zerox80.riotapi.model.RunePerkInfo> map = new java.util.HashMap<>();
        for (JsonNode style : getRunesArray(locale)) {
            for (JsonNode slot : style.path("slots")) {
                for (JsonNode perk : slot.path("runes")) {
                    int id = perk.path("id").asInt();
                    String name = perk.path("name").asText("");
                    String icon = perk.path("icon").asText(""); // e.g., perk-images/...
                    map.put(id, new com.zerox80.riotapi.model.RunePerkInfo(id, name, icon));
                }
            }
        }
        return map;
    }

    private JsonNode getRunesArray(Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        String url = DDRAGON_BASE + "/cdn/" + ver + "/data/" + loc + "/runesReforged.json";
        return getJson(url);
    }

    // ===== Summoner Spells =====
    @Cacheable(cacheNames = "ddragonSummonerSpells", key = "#root.target.getLatestVersion() + '|' + #root.target.resolveLocale(#locale)")
    public Map<Integer, com.zerox80.riotapi.model.SummonerSpellInfo> getSummonerSpells(Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        String url = DDRAGON_BASE + "/cdn/" + ver + "/data/" + loc + "/summoner.json";
        JsonNode root = getJson(url);
        JsonNode data = root.path("data");
        if (!data.isObject()) return Collections.emptyMap();
        Map<Integer, com.zerox80.riotapi.model.SummonerSpellInfo> map = new java.util.HashMap<>();
        data.fields().forEachRemaining(e -> {
            JsonNode n = e.getValue();
            int id = Integer.parseInt(n.path("key").asText("0"));
            String name = n.path("name").asText("");
            String imageFull = n.path("image").path("full").asText("");
            map.put(id, new com.zerox80.riotapi.model.SummonerSpellInfo(id, name, imageFull));
        });
        return map;
    }
}
