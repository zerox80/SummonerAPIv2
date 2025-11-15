// Package declaration: indicates the service-layer namespace for Data Dragon integration utilities
package com.zerox80.riotapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerox80.riotapi.model.ChampionDetail;
import com.zerox80.riotapi.model.ChampionSummary;
import com.zerox80.riotapi.model.PassiveSummary;
import com.zerox80.riotapi.model.SpellSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Service responsible for interacting with Riot's static content APIs (Data Dragon) and
 * the community-driven CommunityDragon dataset. The service centralizes every read-only
 * lookup for champions, localized assets, curated tooltips and sanitized HTML snippets.
 *
 * Responsibilities include:
 * <ul>
 *   <li>Fetching and caching Data Dragon versions, champion lists and champion details</li>
 *   <li>Merging curated local JSON bundles with official Riot data for richer tooltips</li>
 *   <li>Sanitizing HTML fragments so that tooltips can be rendered safely in the browser</li>
 *   <li>Falling back to CommunityDragon data when Riot's payloads omit numeric values</li>
 *   <li>Providing helper utilities to normalize locales, tokens and champion identifiers</li>
 * </ul>
 */
@Service
public class DataDragonService {

    // Base URL for the official Riot Data Dragon CDN
    private static final String DDRAGON_BASE = "https://ddragon.leagueoflegends.com";

    private static final Logger logger = LoggerFactory.getLogger(DataDragonService.class);

    // Primary HTTP/2 client shared with the Riot API client
    private final HttpClient httpClient;
    // Fallback HTTP/1.1 client for legacy/community endpoints that reject HTTP/2
    private final HttpClient fallbackHttp1;
    // Reusable Jackson mapper for JSON parsing
    private final ObjectMapper mapper = new ObjectMapper();
    // Locale used when callers do not provide a preference
    private final String defaultLocale;
    // Custom User-Agent propagated to remote services
    private final String userAgent;
    // Provider used to obtain the proxied (AOP-aware) instance for cacheable/self-invoking methods
    private final ObjectProvider<DataDragonService> selfProvider;
    // Tracks last successful patch version to offer a deterministic fallback
    private volatile String lastKnownVersion;

    private static final String CURATED_BUNDLE_NAME = "champions.json";
    // In-memory cache of curated tooltip bundles per locale to avoid repeated disk access
    private final ConcurrentMap<String, Map<String, LocalAbilities>> curatedChampionCache = new ConcurrentHashMap<>();

    // Conservative HTML sanitization policy for tooltips/descriptions from DDragon/CDragon.
    // Allow common inline formatting and a small set of known game-specific tags used in tooltips
    // (e.g., <magicDamage>, <physicalDamage>, <trueDamage>, <status>, <br>), without any attributes.
    private static final PolicyFactory TOOLTIP_POLICY =
            new HtmlPolicyBuilder()
                    .allowElements("b", "strong", "i", "em", "u", "small", "sup", "sub", "br", "span")
                    .allowElements("magicdamage", "physicaldamage", "truedamage", "status")
                    // No attributes/URLs allowed; compose with basic formatting sanitizer for safety
                    .toFactory()
                    .and(Sanitizers.FORMATTING);

    private static final Pattern TAG_STRIP_PATTERN = Pattern.compile("<[^>]+>");

    
    /**
     * Constructs the service with HTTP client dependencies and runtime configuration.
     *
     * @param riotApiHttpClient shared HTTP client configured with tracing/executor propagation
     * @param defaultLocale default locale configured through application properties
     * @param userAgent descriptive user agent sent to Data Dragon / CommunityDragon
     * @param selfProvider ObjectProvider that allows the bean to call its own proxied methods
     */
    public DataDragonService(HttpClient riotApiHttpClient,
                             @Value("${ddragon.default-locale:en_US}") String defaultLocale,
                             @Value("${app.user-agent:SummonerAPI/2.0 (github.com/zerox80/SummonerAPI)}") String userAgent,
                             ObjectProvider<DataDragonService> selfProvider) {
        this.httpClient = riotApiHttpClient;
        this.fallbackHttp1 = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.defaultLocale = (defaultLocale == null || defaultLocale.isBlank()) ? "en_US" : defaultLocale;
        this.userAgent = (userAgent == null || userAgent.isBlank()) ? "SummonerAPI/2.0 (github.com/zerox80/SummonerAPI)" : userAgent;
        this.selfProvider = selfProvider;
    }

    /**
     * Retrieves either the proxied Spring bean (to honor caching annotations) or falls back
     * to the current instance when running outside the application context (e.g., tests).
     */
    private DataDragonService self() {
        DataDragonService proxy = selfProvider != null ? selfProvider.getIfAvailable() : null;
        return proxy != null ? proxy : this;
    }

    /**
     * Sanitizes tooltips/lore snippets using the allow-list policy defined above.
     * Falls back to a safe plain-text rendering when sanitization fails.
     */
    private String sanitizeHtml(String html) {
        if (html == null || html.isBlank()) return html;
        try {
            return applyTooltipPolicy(html);
        } catch (RuntimeException ex) {
            logger.warn("Tooltip sanitization failed; falling back to plain text", ex);
            return stripTagsToPlainText(html);
        }
    }

    /**
     * Exposed for testing: applies the tooltip policy and returns sanitized HTML.
     */
    protected String applyTooltipPolicy(String html) {
        return TOOLTIP_POLICY.sanitize(html);
    }

    /**
     * Replaces HTML tags with whitespace and collapses escape sequences to produce
     * user-friendly plain text for tooltips that cannot be safely sanitized.
     */
    private String stripTagsToPlainText(String html) {
        String withoutTags = TAG_STRIP_PATTERN.matcher(html).replaceAll(" ");
        String unescaped = HtmlUtils.htmlUnescape(withoutTags);
        return unescaped.replaceAll("\\s+", " ").trim();
    }

    /**
     * Helper that counts digits within a string to heuristically detect missing numbers
     * when validating tooltips.
     */
    private int countDigits(String s) {
        if (s == null || s.isBlank()) return 0;
        int c = 0;
        for (int i = 0; i < s.length(); i++) if (Character.isDigit(s.charAt(i))) c++;
        return c;
    }

    /**
     * Collects tooltip tokens (damage numbers, cooldown strings, etc.) from a Data Dragon
     * spell node so that they can be re-used across multiple abilities when rendering rich
     * tooltips. The resulting map mirrors Riot's token names (e.g., e1, cooldown, slowamount).
     */
    private void collectGlobalDDragonValues(JsonNode spellNode, Map<String,String> out) {
        if (spellNode == null || out == null) return;
        // datavalues
        JsonNode datavalues = spellNode.path("datavalues");
        if (datavalues != null && datavalues.isObject()) {
            datavalues.fields().forEachRemaining(e -> {
                String k = e.getKey();
                String v = e.getValue().asText("");
                if (!k.isBlank() && !v.isBlank()) out.putIfAbsent(k.toLowerCase(Locale.ROOT), v);
            });
        }

        // vars (coefficients)
        JsonNode vars = spellNode.path("vars");
        if (vars != null && vars.isArray()) {
            for (JsonNode v : vars) {
                String key = v.path("key").asText("").toLowerCase(Locale.ROOT);
                if (key.isBlank()) continue;
                JsonNode coeff = v.path("coeff");
                String coeffStr;
                if (coeff.isArray()) {
                    List<String> parts = new ArrayList<>();
                    coeff.forEach(n -> parts.add(formatCoeff(n.asDouble(0.0))));
                    coeffStr = String.join("/", parts);
                } else if (coeff.isNumber()) {
                    coeffStr = formatCoeff(coeff.asDouble(0.0));
                } else {
                    coeffStr = coeff.asText("");
                }
                String link = mapLinkToStatLabel(v.path("link").asText(""));
                String val = coeffStr + (link.isEmpty() ? "" : (" " + link));
                if (!coeffStr.isBlank()) out.putIfAbsent(key, val);
            }
        }
        // cooldown/cost burns as generic values
        String cooldown = spellNode.path("cooldownBurn").asText("");
        if (!cooldown.isBlank()) out.putIfAbsent("cooldown", cooldown);
        String cost = spellNode.path("costBurn").asText("");
        if (!cost.isBlank()) out.putIfAbsent("cost", cost);
        // Sometimes tokens like slowamount exist in other spells' datavalues in some champs; already covered above.
    }

    /**
     * Normalizes champion identifiers by stripping non alphabetic characters and limiting
     * length. This prevents malformed champion IDs from being used in downstream lookups.
     */
    private String sanitizeChampionIdBasic(String raw) {
        if (raw == null) return null;
        String cleaned = raw.replaceAll("[^A-Za-z]", "");
        if (cleaned.length() > 30) {
            cleaned = cleaned.substring(0, 30);
        }
        return cleaned.isBlank() ? null : cleaned;
    }

    /**
     * Attempts to map a user-provided champion identifier to the canonical ID exposed by
     * Data Dragon by comparing both champion IDs and champion names in a case-insensitive way.
     *
     * @return The canonical champion ID if a match is found, otherwise {@code null}.
     */
    private String resolveCanonicalChampionId(String candidateId, String version, String localeTag) throws IOException, InterruptedException {
        if (candidateId == null || candidateId.isBlank()) {
            return null;
        }
        String lowerCandidate = candidateId.toLowerCase(Locale.ROOT);
        for (ChampionSummary summary : self().getChampionSummariesCached(version, localeTag)) {
            String summaryIdSanitized = sanitizeChampionIdBasic(summary.getId());
            if (summaryIdSanitized == null) continue;
            if (summaryIdSanitized.equalsIgnoreCase(candidateId) || summaryIdSanitized.toLowerCase(Locale.ROOT).equals(lowerCandidate)) {
                return summary.getId();
            }
            String summaryNameSanitized = sanitizeChampionIdBasic(summary.getName());
            if (summaryNameSanitized != null && (summaryNameSanitized.equalsIgnoreCase(candidateId) || summaryNameSanitized.toLowerCase(Locale.ROOT).equals(lowerCandidate))) {
                return summary.getId();
            }
        }
        return null;
    }

    
    /**
     * Loads the complete list of Data Dragon patch versions and caches the result for twelve hours.
     * The first entry is stored as {@link #lastKnownVersion} for subsequent fallbacks.
     */
    @Cacheable(cacheNames = "ddragonVersions", unless = "#result == null || #result.isEmpty()")
    public List<String> getAllVersions() {
        String url = DDRAGON_BASE + "/api/versions.json";
        try {
            JsonNode node = getJson(url);
            if (node.isArray()) {
                List<String> versions = new ArrayList<>();
                node.forEach(n -> versions.add(n.asText()));
                if (!versions.isEmpty()) {
                    lastKnownVersion = versions.get(0);
                }
                return versions;
            }
        } catch (Exception e) {
            // ignore and fall through
        }
        return Collections.emptyList();
    }

    
    /**
     * Convenience accessor that returns the newest Data Dragon version. When the remote
     * endpoint cannot be reached, the method falls back to {@link #lastKnownVersion}.
     */
    public String getLatestVersion() {
        List<String> versions = getAllVersions();
        if (!versions.isEmpty()) {
            lastKnownVersion = versions.get(0);
            return versions.get(0);
        }
        return (lastKnownVersion != null && !lastKnownVersion.isBlank()) ? lastKnownVersion : "latest";
    }

    
    /**
     * Returns a short patch string (major.minor) derived from the latest version
     * so that downstream code can reference aggregated statistics per patch.
     */
    public String getLatestShortPatch() {
        String v = getLatestVersion();
        // e.g., 15.18.1 -> 15.18
        String[] parts = v.split("\\.");
        if (parts.length >= 2) return parts[0] + "." + parts[1];
        return v;
    }

    
    /**
     * Maps arbitrary {@link Locale} inputs (browser language, HTTP headers, etc.) to the
     * locale identifiers expected by Data Dragon (e.g., {@code de_DE}, {@code en_US}).
     */
    public String resolveLocale(Locale locale) {
        if (locale == null) return defaultLocale;
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
        return defaultLocale;
    }

    
    /**
     * Returns all champion summaries for the newest patch, localized to the provided locale.
     * Internally delegates to the cache-aware method to ensure results are reused.
     */
    public List<ChampionSummary> getChampionSummaries(Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        return self().getChampionSummariesCached(ver, loc);
    }

    
    /**
     * Loads the localized champion index for the supplied version and locale.
     * The list is cached per version/locale combination because the upstream payload is static.
     */
    @Cacheable(cacheNames = "ddragonChampionList", key = "#version + '|' + #localeTag")
    public List<ChampionSummary> getChampionSummariesCached(String version, String localeTag) throws IOException, InterruptedException {
        String url = DDRAGON_BASE + "/cdn/" + version + "/data/" + localeTag + "/champion.json";
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
        return list.stream()
                .sorted(Comparator.comparing(ChampionSummary::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    
    /**
     * Fetches the full detail payload (lore, tags, spells, passive) for a champion in a given locale.
     * Applies curated overrides when available and gracefully falls back to English resources.
     */
    public ChampionDetail getChampionDetail(String championId, Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        return self().getChampionDetailCached(championId, ver, loc);
    }

    
    /**
     * Cacheable variant of {@link #getChampionDetail(String, Locale)} that exposes deterministic
     * keys (champion/version/locale) so downstream services can render champion pages instantly.
     */
    @Cacheable(cacheNames = "ddragonChampionDetail", key = "T(com.zerox80.riotapi.service.DataDragonService).safeLower(#championId) + '|' + #version + '|' + #localeTag")
    public ChampionDetail getChampionDetailCached(String championId, String version, String localeTag) throws IOException, InterruptedException {
        String cid = sanitizeChampionIdBasic(championId);
        if (cid == null) return null;
        String canonicalId = resolveCanonicalChampionId(cid, version, localeTag);
        if (canonicalId != null) {
            cid = canonicalId;
        }
        JsonNode root = getJson(DDRAGON_BASE + "/cdn/" + version + "/data/" + localeTag + "/champion/" + cid + ".json");
        JsonNode data = root.path("data").path(cid);
        if (data.isMissingNode()) {
            return null;
        }
        String id = data.path("id").asText(cid);
        String name = data.path("name").asText("");
        String title = data.path("title").asText("");
        String lore = data.path("lore").asText("");
        List<String> tags = new ArrayList<>();
        if (data.path("tags").isArray()) data.path("tags").forEach(t -> tags.add(t.asText()));
        String imageFull = data.path("image").path("full").asText("");

        // Try to load local, versioned abilities (e.g., abilities/<locale>/anivia.json)
        LocalAbilities local = loadLocalAbilities(id, localeTag);
        if (local != null && (local.passive != null || (local.spells != null && !local.spells.isEmpty()))) {
            applyChampionSpecificTooltipOverrides(id, local.localeTag, local.spells);
            // Prefer curated lore if available, otherwise use DDragon lore
            String curatedLore = (local.lore != null && !local.lore.isBlank()) ? local.lore : lore;
            return new ChampionDetail(id, name, title, curatedLore, tags, imageFull,
                    local.passive, local.spells == null ? java.util.Collections.emptyList() : local.spells);
        }

        LocalAbilities generated = buildChampionAbilities(data, localeTag);
        if ((generated == null || generated.spells == null || generated.spells.isEmpty())
                && !"en_US".equalsIgnoreCase(localeTag)) {
            try {
                JsonNode enRoot = getJson(DDRAGON_BASE + "/cdn/" + version + "/data/en_US/champion/" + cid + ".json");
                JsonNode enData = enRoot.path("data").path(cid);
                if (!enData.isMissingNode()) {
                    generated = buildChampionAbilities(enData, "en_US");
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception ignore) {
                // fallback failed; keep previous result
            }
        }
        if (generated != null && (generated.passive != null || (generated.spells != null && !generated.spells.isEmpty()))) {
            applyChampionSpecificTooltipOverrides(id, generated.localeTag, generated.spells);
            String finalLore = selectPreferredString(local != null ? local.lore : null, lore);
            return new ChampionDetail(id, name, title, finalLore, tags, imageFull,
                    generated.passive, generated.spells == null ? java.util.Collections.emptyList() : generated.spells);
        }

        // Default: lore only (no passive, no spells)
        return new ChampionDetail(id, name, title, lore, tags, imageFull, null, java.util.Collections.emptyList());
    }

    /**
     * Small immutable holder containing lore, passive and spell details either loaded from
     * curated JSON bundles or synthesized from Data Dragon payloads. Keeps track of the locale
     * that produced the data so overrides can respect language boundaries.
     */
    private static class LocalAbilities {
        final String lore;
        final PassiveSummary passive;
        final List<SpellSummary> spells;
        final String localeTag;
        LocalAbilities(String lore, PassiveSummary p, List<SpellSummary> s, String localeTag) {
            this.lore = lore;
            this.passive = p;
            this.spells = s;
            this.localeTag = localeTag;
        }
    }

    /**
     * Loads champion abilities from bundled resources (when available) or from per-champion
     * JSON files stored under {@code abilities/<locale>/<champion>.json}. These curated files
     * allow us to override incomplete Riot tooltips with manually authored content.
     */
    private LocalAbilities loadLocalAbilities(String championId, String ddragonLocale) {
        LocalAbilities curated = loadCuratedAbilitiesFromBundle(championId, ddragonLocale);
        if (curated == null && !localeEquals(ddragonLocale, defaultLocale)) {
            curated = loadCuratedAbilitiesFromBundle(championId, defaultLocale);
        }
        if (curated != null) {
            return curated;
        }
        try {
            String loc = (ddragonLocale == null || ddragonLocale.isBlank()) ? defaultLocale : ddragonLocale;
            String cid = championId == null ? "" : championId.trim();
            if (cid.isEmpty()) return null;
            String cidLower = cid.toLowerCase(Locale.ROOT);

            InputStream stream = openLocalizedAbilityStream(loc, cidLower);
            if (stream == null) return null;

            JsonNode root;
            try (InputStream is = stream) {
                root = mapper.readTree(is);
            }
            // Passive
            PassiveSummary passive = null;
            JsonNode p = root.path("passive");
            if (p != null && p.isObject()) {
                String pName = p.path("name").asText("");
                String pDesc = p.path("description").asText("");
                String pImg = p.path("imageFull").asText("");
                if (!pName.isBlank() || !pDesc.isBlank() || !pImg.isBlank()) {
                    passive = new PassiveSummary(pName, sanitizeHtml(pDesc), pImg);
                }
            }
            // Spells
            List<SpellSummary> spells = new ArrayList<>();
            JsonNode sArr = root.path("spells");
            if (sArr != null && sArr.isArray()) {
                for (JsonNode s : sArr) {
                    String sid = s.path("id").asText("");
                    String sname = s.path("name").asText("");
                    String tip = s.path("tooltip").asText("");
                    String img = s.path("imageFull").asText("");
                    SpellSummary sum = new SpellSummary(sid, sname, sanitizeHtml(tip), img);
                    if (s.hasNonNull("cooldown")) sum.setCooldown(s.path("cooldown").asText(""));
                    if (s.hasNonNull("cost")) sum.setCost(s.path("cost").asText(""));
                    if (s.hasNonNull("range")) sum.setRange(s.path("range").asText(""));
                    if (s.hasNonNull("damage")) sum.setDamage(s.path("damage").asText(""));
                    if (s.hasNonNull("scaling")) sum.setScaling(s.path("scaling").asText(""));
                    if (s.has("notes") && s.path("notes").isArray()) {
                        java.util.List<String> notes = new java.util.ArrayList<>();
                        for (JsonNode n : s.path("notes")) notes.add(n.asText(""));
                        sum.setNotes(notes);
                    }
                    spells.add(sum);
                }
            }
            return new LocalAbilities(null, passive, spells, loc);
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * Attempts to resolve a champion entry from the curated bundle (used for locales that
     * would otherwise rely solely on Riot's payload). Results are cached in-memory per locale.
     */
    private LocalAbilities loadCuratedAbilitiesFromBundle(String championId, String localeTag) {
        if (championId == null || championId.isBlank()) return null;
        String canonicalLocale = normalizeLocaleTag(localeTag);
        String cacheKey = canonicalLocale.toLowerCase(Locale.ROOT);
        Map<String, LocalAbilities> byChampion = curatedChampionCache.computeIfAbsent(cacheKey, key -> loadCuratedLocaleMap(canonicalLocale));
        if (byChampion == null || byChampion.isEmpty()) return null;
        return byChampion.get(championId.toLowerCase(Locale.ROOT));
    }

    /**
     * Loads and deserializes the curated bundle ({@link #CURATED_BUNDLE_NAME}) for a specific locale.
     *
     * @return map of lower-cased champion IDs to {@link LocalAbilities} or {@code null} when missing
     */
    private Map<String, LocalAbilities> loadCuratedLocaleMap(String canonicalLocale) {
        String path = String.format("abilities/%s/%s", canonicalLocale, CURATED_BUNDLE_NAME);
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (stream == null) {
            String lowerLocale = canonicalLocale.toLowerCase(Locale.ROOT);
            path = String.format("abilities/%s/%s", lowerLocale, CURATED_BUNDLE_NAME);
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        }
        if (stream == null) {
            return Collections.emptyMap();
        }
        try (InputStream is = stream) {
            JsonNode root = mapper.readTree(is);
            if (root == null || !root.isObject()) {
                return Collections.emptyMap();
            }
            Map<String, LocalAbilities> map = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String champKey = entry.getKey();
                JsonNode node = entry.getValue();
                LocalAbilities abilities = parseCuratedChampionNode(node, canonicalLocale);
                if (abilities != null) {
                    map.put(champKey.toLowerCase(Locale.ROOT), abilities);
                }
            }
            return map;
        } catch (Exception ex) {
            logger.warn("Failed to parse curated abilities bundle for locale {}", canonicalLocale, ex);
            return Collections.emptyMap();
        }
    }

    private LocalAbilities parseCuratedChampionNode(JsonNode node, String localeTag) {
        if (node == null || !node.isObject()) return null;

        String lore = sanitizeHtml(node.path("lore").asText(null));
        if (lore != null && lore.isBlank()) lore = null;

        PassiveSummary passive = null;
        JsonNode passiveNode = node.path("passive");
        if (passiveNode != null && passiveNode.isObject()) {
            String pName = passiveNode.path("name").asText("");
            String pDesc = sanitizeHtml(passiveNode.path("description").asText(""));
            String pImg = passiveNode.path("imageFull").asText("");
            if (!pName.isBlank() || !pDesc.isBlank() || !pImg.isBlank()) {
                passive = new PassiveSummary(pName, pDesc, pImg);
            }
        }

        List<SpellSummary> spells = new ArrayList<>();
        JsonNode spellsNode = node.path("spells");
        if (spellsNode != null && spellsNode.isArray()) {
            for (JsonNode spellNode : spellsNode) {
                if (spellNode == null || !spellNode.isObject()) continue;
                String sid = spellNode.path("id").asText("");
                if (sid.isBlank()) continue;
                String sname = spellNode.path("name").asText("");
                String tooltip = sanitizeHtml(spellNode.path("description").asText(""));
                String img = spellNode.path("imageFull").asText("");
                SpellSummary summary = new SpellSummary(sid, sname, tooltip, img);
                String cooldown = spellNode.path("cooldown").asText("");
                if (!cooldown.isBlank()) summary.setCooldown(cooldown);
                String cost = spellNode.path("cost").asText("");
                if (!cost.isBlank()) summary.setCost(cost);
                String range = spellNode.path("range").asText("");
                if (!range.isBlank()) summary.setRange(range);
                String damage = spellNode.path("damage").asText("");
                if (!damage.isBlank()) summary.setDamage(damage);
                String scaling = spellNode.path("scaling").asText("");
                if (!scaling.isBlank()) summary.setScaling(scaling);
                JsonNode notesNode = spellNode.path("notes");
                if (notesNode != null && notesNode.isArray()) {
                    List<String> notes = new ArrayList<>();
                    notesNode.forEach(n -> {
                        String text = sanitizeHtml(n.asText(""));
                        if (!text.isBlank()) notes.add(text);
                    });
                    if (!notes.isEmpty()) {
                        summary.setNotes(notes);
                    }
                }
                spells.add(summary);
            }
        }

        if (lore == null && passive == null && spells.isEmpty()) {
            return null;
        }

        return new LocalAbilities(lore, passive, spells, localeTag);
    }

    /**
     * Converts locale tags to the canonical underscore format used by bundled assets.
     */
    private String normalizeLocaleTag(String localeTag) {
        String loc = (localeTag == null || localeTag.isBlank()) ? defaultLocale : localeTag;
        return loc.replace('-', '_');
    }

    /**
     * Parses the standard Data Dragon champion payload and builds {@link LocalAbilities}
     * from it by extracting passive/spell details, resolving tooltip tokens and sanitizing
     * the resulting HTML.
     */
    private LocalAbilities buildChampionAbilities(JsonNode championData, String localeTag) {
        if (championData == null || championData.isMissingNode()) {
            return null;
        }

        PassiveSummary passive = null;
        JsonNode passiveNode = championData.path("passive");
        if (passiveNode.isObject()) {
            String pName = passiveNode.path("name").asText("");
            String pDesc = passiveNode.path("description").asText("");
            String pImg = passiveNode.path("image").path("full").asText("");
            if (!pName.isBlank() || !pDesc.isBlank() || !pImg.isBlank()) {
                passive = new PassiveSummary(pName, sanitizeHtml(pDesc), pImg);
            }
        }

        List<SpellSummary> spells = new ArrayList<>();
        JsonNode spellsArray = championData.path("spells");
        if (spellsArray != null && spellsArray.isArray()) {
            Map<String, String> globalValues = new HashMap<>();
            spellsArray.forEach(node -> collectGlobalDDragonValues(node, globalValues));

            List<String> cdragonTooltips = Collections.emptyList();
            String keyStr = championData.path("key").asText("");
            if (!keyStr.isBlank()) {
                try {
                    int champKey = Integer.parseInt(keyStr);
                    cdragonTooltips = fetchCDragonResolvedTooltips(champKey, localeTag);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    cdragonTooltips = Collections.emptyList();
                } catch (Exception ignore) {
                    cdragonTooltips = Collections.emptyList();
                }
            }

            for (int i = 0; i < spellsArray.size(); i++) {
                JsonNode spellNode = spellsArray.get(i);
                String sid = spellNode.path("id").asText("");
                String sname = spellNode.path("name").asText("");
                String tooltip = renderSpellTooltip(spellNode, globalValues);
                if (looksIncompleteTooltip(tooltip, localeTag)) {
                    if (cdragonTooltips != null && i < cdragonTooltips.size()) {
                        String resolved = cdragonTooltips.get(i);
                        if (resolved != null && !resolved.isBlank()) {
                            tooltip = resolved;
                        }
                    }
                }
                String img = spellNode.path("image").path("full").asText("");
                SpellSummary summary = new SpellSummary(sid, sname, sanitizeHtml(tooltip), img);

                String cooldown = spellNode.path("cooldownBurn").asText("");
                if (!cooldown.isBlank()) summary.setCooldown(cooldown);
                String cost = spellNode.path("costBurn").asText("");
                if (!cost.isBlank()) summary.setCost(cost);
                String range = spellNode.path("rangeBurn").asText("");
                if (!range.isBlank()) summary.setRange(range);

                JsonNode effectBurn = spellNode.path("effectBurn");
                if (effectBurn.isArray()) {
                    StringBuilder dmgBuilder = new StringBuilder();
                    for (int e = 1; e < effectBurn.size(); e++) {
                        String val = effectBurn.get(e).asText("").trim();
                        if (val.isBlank()) continue;
                        if (dmgBuilder.length() > 0) dmgBuilder.append(" / ");
                        dmgBuilder.append(val);
                    }
                    String dmg = dmgBuilder.toString();
                    if (!dmg.isBlank()) summary.setDamage(dmg);
                }

                JsonNode levelTip = spellNode.path("leveltip");
                if (levelTip.isObject()) {
                    JsonNode labels = levelTip.path("label");
                    JsonNode effects = levelTip.path("effect");
                    if (labels.isArray()) {
                        List<String> notes = new ArrayList<>();
                        for (int j = 0; j < labels.size(); j++) {
                            String label = labels.get(j).asText("").trim();
                            String effect = (effects.isArray() && j < effects.size()) ? effects.get(j).asText("").trim() : "";
                            StringBuilder sb = new StringBuilder();
                            if (!label.isEmpty()) sb.append(label);
                            if (!effect.isEmpty()) {
                                if (sb.length() > 0) sb.append(": ");
                                sb.append(effect);
                            }
                            String note = sb.toString();
                            if (!note.isBlank()) notes.add(note);
                        }
                        if (!notes.isEmpty()) summary.setNotes(notes);
                    }
                }

                spells.add(summary);
            }
        }

        if (passive == null && spells.isEmpty()) {
            return null;
        }
        String lore = championData.path("lore").asText("");
        return new LocalAbilities(lore.isBlank() ? null : sanitizeHtml(lore), passive, spells, localeTag);
    }

    /**
     * Returns the first non-blank string, preferring {@code primary} and falling back to {@code fallback}.
     */
    private static String selectPreferredString(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }

    private InputStream openLocalizedAbilityStream(String locale, String cidLower) {
        String path = String.format("abilities/%s/%s.json", locale, cidLower);
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    private boolean localeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.replace('-', '_').equalsIgnoreCase(b.replace('-', '_'));
    }

    
    /**
     * Convenience overload that renders a single spell tooltip without supplying
     * any global Data Dragon values.
     */
    private String renderSpellTooltip(JsonNode spellNode) {
        return renderSpellTooltip(spellNode, Collections.emptyMap());
    }

    /**
     * Resolves Riot's templated tooltip strings by substituting placeholders such as
     * {@code {{ e1 }}} with concrete damage/cooldown numbers. The method merges
     * per-spell values with the optional {@code globalValues} map to mimic Riot's client.
     */
    private String renderSpellTooltip(JsonNode spellNode, Map<String,String> globalValues) {
        if (spellNode == null || spellNode.isMissingNode()) return "";
        String raw = spellNode.path("tooltip").asText("");
        if (raw == null || raw.isBlank()) return raw;

        Map<String, String> values = new HashMap<>();

        // effectBurn: array where indexes align to e1, e2, ... (index 0 is often null)
        JsonNode effectBurn = spellNode.path("effectBurn");
        if (effectBurn != null && effectBurn.isArray()) {
            for (int i = 0; i < effectBurn.size(); i++) {
                String val = effectBurn.get(i).asText("");
                if (i > 0 && !val.isBlank()) {
                    values.put("e" + i, val);
                }
            }
        }

        // cooldown/cost strings
        String cooldown = spellNode.path("cooldownBurn").asText("");
        if (!cooldown.isBlank()) values.put("cooldown", cooldown);
        String cost = spellNode.path("costBurn").asText("");
        if (!cost.isBlank()) values.put("cost", cost);

        // datavalues: keys may match tokens seen in tooltips (e.g., totaldamage, slowduration)
        JsonNode datavalues = spellNode.path("datavalues");
        if (datavalues != null && datavalues.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = datavalues.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> e = it.next();
                String key = e.getKey();
                String val = e.getValue().asText("");
                if (!key.isBlank() && !val.isBlank()) values.put(key.toLowerCase(Locale.ROOT), val);
            }
        }

        // vars: provide coefficients for a1, a2... Use a readable label with AP/AD when possible.
        JsonNode vars = spellNode.path("vars");
        if (vars != null && vars.isArray()) {
            for (JsonNode v : vars) {
                String key = v.path("key").asText(""); // e.g., a1
                if (key.isBlank()) continue;
                String link = v.path("link").asText(""); // e.g., spelldamage, attackdamage, armor
                JsonNode coeffNode = v.path("coeff");
                String coeffStr;
                if (coeffNode.isArray()) {
                    // Join array coefficients with '/'
                    List<String> parts = new ArrayList<>();
                    coeffNode.forEach(n -> parts.add(formatCoeff(n.asDouble(0.0))));
                    coeffStr = String.join("/", parts);
                } else if (coeffNode.isNumber()) {
                    coeffStr = formatCoeff(coeffNode.asDouble(0.0));
                } else {
                    coeffStr = coeffNode.asText("");
                }
                String label = mapLinkToStatLabel(link);
                String val = coeffStr + (label.isEmpty() ? "" : (" " + label));
                values.put(key.toLowerCase(Locale.ROOT), val);
            }
        }

        // Replace tokens {{ ... }} (support prefixes like spell.xyz:token -> use right-most token)
        Pattern p = Pattern.compile("\\{\\{\\s*([^}]+?)\\s*\\}}", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(raw);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String token = m.group(1);
            String norm = normalizeToken(token);
            String replacement = values.getOrDefault(norm, "");
            if (replacement.isEmpty() && globalValues != null) {
                replacement = globalValues.getOrDefault(norm, "");
            }
            // Fallback: also try without trailing digits if not found (e.g., a1 -> a)
            if (replacement.isEmpty() && norm.length() > 1 && Character.isDigit(norm.charAt(norm.length()-1))) {
                String trimmed = norm.replaceAll("\\d+$", "");
                replacement = values.getOrDefault(trimmed, "");
                if (replacement.isEmpty() && globalValues != null) {
                    replacement = globalValues.getOrDefault(trimmed, "");
                }
            }
            // Avoid inserting raw braces if unknown; use empty string
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);

        String out = sb.toString();
        // Heuristic patching: if we still see phrases like "for seconds" or bare "%" without a number,
        // try to inject a plausible value from collected effect burns (e1/e2/...) or datavalues.
        out = patchMissingNumbers(out, values);

        // Some tooltips contain special tags like <status>...</status>; keep as-is for utext
        return out;
    }

    /**
     * Applies heuristic replacements when Riot's templates do not contain the numbers
     * necessary to make a tooltip readable (e.g., "for seconds" without any digits).
     */
    private String patchMissingNumbers(String text, Map<String,String> values){
        if (text == null || text.isBlank()) return text;
        String result = text;

        // Generic cleanup: collapse immediate duplicate numbers (e.g., "1 1" or "0.75 0.75"). Accept NBSP as whitespace.
        result = result.replaceAll("(?i)\\b(\\d+(?:[\\.,]\\d+)?)(?:[\\s\\u00A0]+)\\1\\b", "$1");
        // Typo/format fixes: "Sekundenn" and "Sekunden(n)"/"Sekunde(n)"
        result = result.replaceAll("(?i)sekundenn", "Sekunden");
        result = result.replaceAll("(?i)sekunden\\(n\\)", "Sekunden");
        result = result.replaceAll("(?i)sekunde\\(n\\)", "Sekunde");

        // Helper to pick candidate from e1/e2/... that looks like a duration (<= 10 sec)
        java.util.function.Predicate<java.util.List<Double>> isSeconds = nums -> {
            if (nums.isEmpty()) return false;
            for (Double d : nums) { if (d == null || d <= 0 || d > 10) return false; }
            return true;
        };
        // Helper to pick candidate that looks like a percent (0..300)
        java.util.function.Predicate<java.util.List<Double>> isPercent = nums -> {
            if (nums.isEmpty()) return false;
            for (Double d : nums) { if (d == null || d <= 0 || d > 300) return false; }
            return true;
        };

        String secondsCandidate = pickJoinedCandidate(values, isSeconds);
        // English "for seconds" variants (allow tags between words, handle 'second(s)' or 'sec')
        if (secondsCandidate != null && !secondsCandidate.isBlank()) {
            boolean hasNumberSeconds = result.matches(".*\\d+(?:[\\s\\u00A0])*(?:second(?:s|\\(s\\))?|sec(?:\\.|onds?)?).*");
            if (!hasNumberSeconds) {
                // Insert number before the unit, allowing optional inline HTML tags between 'for' and the unit
                result = result.replaceFirst(
                        "(?i)for(?:[\\s\\u00A0]+)(?:<[^>]+>(?:[\\s\\u00A0])*)*(second(?:s|\\(s\\))?|sec(?:\\.|onds?)?)",
                        "for " + java.util.regex.Matcher.quoteReplacement(secondsCandidate) + " $1");
            }
            // German "Sekunden"/"Sekunde" (allow tags in between)
            boolean hasNumberSek = result.matches(".*\\d+(,\\d+)?(?:[\\s\\u00A0])*sekunde(n)?.*");
            if (!hasNumberSek && (result.toLowerCase(Locale.ROOT).contains("sekunde"))) {
                result = result.replaceFirst(
                        "(?i)(?:<[^>]+>(?:[\\s\\u00A0])*)*sekunde(n)?",
                        java.util.regex.Matcher.quoteReplacement(secondsCandidate) + " Sekunden");
            }
        }

        // If two numbers appear before seconds and the second is decimal, keep the decimal one (allow NBSP)
        result = result.replaceAll("(?i)\\b(\\d+(?:[\\.,]\\d+)?)\\s*\\u00A0?\\s+(\\d+[\\.,]\\d+)\\s*\\u00A0?\\s*(sekunde(n)?|second(s)?)\\b", "$2 $3");
        // Collapse patterns like "1 1 Sekunden" or "1 1 seconds" -> "1.1 Sekunden/seconds" (allow NBSP)
        result = result.replaceAll("(?i)\\b(\\d)\\s*\\u00A0?\\s+(\\d)\\s*\\u00A0?\\s*(sekunde(n)?|second(s)?)\\b", "$1.$2 $3");
        // Also allow optional HTML tags between the numbers
        result = result.replaceAll("(?is)\\b(\\d)(?:[\\s\\u00A0]+|<[^>]+>)+(\\d)(?:[\\s\\u00A0]+|<[^>]+>)+(sekunde(n)?|second(s)?)\\b", "$1.$2 $3");
        // If pattern like '1 <tags> 1.1 Sekunden' occurs, keep the decimal number
        result = result.replaceAll("(?is)\\b(\\d+(?:[\\.,]\\d+)?)(?:[\\s\\u00A0]+|<[^>]+>)+(\\d+[\\.,]\\d+)(?:[\\s\\u00A0]+|<[^>]+>)+(sekunde(n)?|second(s)?)\\b", "$2 $3");

        // Damage candidate insertion for magic damage when numbers are missing
        String dmgCandidate = pickDamageCandidate(values);
        if (dmgCandidate != null && !dmgCandidate.isBlank()) {
            // Empty magicDamage tag
            if (result.contains("<magicDamage></magicDamage>")) {
                result = result.replace("<magicDamage></magicDamage>", "<magicDamage>" + java.util.regex.Matcher.quoteReplacement(dmgCandidate) + "</magicDamage>");
            }
            // German "magischen Schaden" without nearby numbers
            if (result.toLowerCase(Locale.ROOT).contains("magischen schaden") && !result.matches(".*\\d+(?:[\\.,]\\d+)?[^\\n\\r]{0,40}magischen schaden.*")) {
                result = result.replaceFirst("(?i)magischen\\s+schaden", java.util.regex.Matcher.quoteReplacement(dmgCandidate + " magischen Schaden"));
            }
            // English "magic damage" without nearby numbers
            if (result.toLowerCase(Locale.ROOT).contains("magic damage") && !result.matches(".*\\d+(?:[\\.,]\\d+)?[^\\n\\r]{0,40}magic damage.*")) {
                result = result.replaceFirst("(?i)magic\\s+damage", java.util.regex.Matcher.quoteReplacement(dmgCandidate + " magic damage"));
            }
        }

        // Small flat number for DE phrase "verringerten normalen Schaden" if missing
        if (result.toLowerCase(Locale.ROOT).contains("verringerten normalen schaden") && !result.matches(".*\\d+(?:[\\.,]\\d+)?[^\\n\\r]{0,40}verringerten normalen schaden.*")) {
            String small = pickSmallNumberCandidate(values, 20.0);
            if (small != null && !small.isBlank()) {
                result = result.replaceFirst("(?i)um\\s+(?:<[^>]+>\\s*)*verringerten\\s+normalen\\s+schaden", java.util.regex.Matcher.quoteReplacement("um " + small + " verringerten normalen Schaden"));
            }
        }

        // Normalize spaces around NBSP: collapse sequences like " \u00A0" or "\u00A0  " to a single NBSP
        result = result.replaceAll(" *\u00A0+ *", "\u00A0");
        // Collapse excessive regular spaces
        result = result.replaceAll("[ ]{2,}", " ").trim();
        return result;
    }

    /**
     * Picks a reasonable damage string (e.g., "80/110/140") from the supplied token map by
     * preferring entries that look like damage/eN tokens and discarding absurd values.
     */
    private String pickDamageCandidate(Map<String,String> values) {
        if (values == null || values.isEmpty()) return null;
        String best = null; double bestAvg = -1.0;
        for (Map.Entry<String,String> e : values.entrySet()) {
            String k = e.getKey(); String v = e.getValue();
            if (k == null || v == null) continue;
            // prefer explicit damage keys or general eN
            if (!(k.matches("e\\d+") || k.contains("damage") || k.contains("dmg") || k.contains("magic"))) continue;
            java.util.List<Double> nums = parseNumbers(v);
            if (nums.isEmpty()) continue;
            // Typical damage range safeguard
            boolean ok = true; for (Double d : nums) { if (d == null || d <= 0 || d > 600) { ok = false; break; } }
            if (!ok) continue;
            double avg = nums.stream().mapToDouble(d -> d).average().orElse(0);
            if (avg > bestAvg) { bestAvg = avg; best = v; }
        }
        return best;
    }

    /**
     * Selects the smallest positive value below {@code max} – handy for cooldown/second heuristics.
     */
    private String pickSmallNumberCandidate(Map<String,String> values, double max) {
        if (values == null || values.isEmpty()) return null;
        String best = null; double bestVal = Double.MAX_VALUE;
        for (Map.Entry<String,String> e : values.entrySet()) {
            String v = e.getValue();
            if (v == null || v.isBlank()) continue;
            java.util.List<Double> nums = parseNumbers(v);
            for (Double d : nums) {
                if (d != null && d > 0 && d <= max) {
                    // pick the smallest positive value within cap
                    if (d < bestVal) { bestVal = d; best = (d % 1 == 0 ? String.valueOf(d.intValue()) : formatCoeff(d)); }
                }
            }
        }
        return best;
    }

    /**
     * Finds a slash-separated value whose parsed numbers satisfy the provided validator.
     */
    private String pickJoinedCandidate(Map<String,String> values, java.util.function.Predicate<java.util.List<Double>> validator){
        if (values == null || values.isEmpty()) return null;
        String best = null; double bestAvg = -1.0;
        for (Map.Entry<String,String> e : values.entrySet()) {
            String k = e.getKey(); String v = e.getValue();
            if (k == null || v == null) continue;
            if (!k.matches("e\\d+|.*duration.*|.*time.*|.*stun.*|.*root.*|.*immobil.*|.*sleep.*|.*knock.*|.*freeze.*")) continue;
            java.util.List<Double> nums = parseNumbers(v);
            if (!validator.test(nums)) continue;
            double avg = nums.stream().mapToDouble(d -> d).average().orElse(0);
            if (avg > bestAvg) { bestAvg = avg; best = v; }
        }
        return best;
    }

    /**
     * Attempts to find a candidate that clearly represents a percentage/slow modifier.
     */
    private String pickPercentCandidate(Map<String,String> values){
        if (values == null || values.isEmpty()) return null;
        // Prefer keys that suggest percent/slow/movement speed
        String best = null; double bestAvg = -1.0;
        for (Map.Entry<String,String> e : values.entrySet()) {
            String k = e.getKey(); String v = e.getValue();
            if (k == null || v == null) continue;
            if (!(k.contains("percent") || k.contains("slow") || k.contains("movement") || k.contains("speed") || k.contains("ms") || k.contains("hp") || k.contains("health"))) continue;
            java.util.List<Double> nums = parseNumbers(v);
            if (nums.isEmpty()) continue;
            // Accept typical percent ranges
            boolean ok = true; for (Double d : nums) { if (d <= 0 || d > 300) { ok = false; break; } }
            if (!ok) continue;
            double avg = nums.stream().mapToDouble(d -> d).average().orElse(0);
            if (avg > bestAvg) { bestAvg = avg; best = v; }
        }
        if (best != null) return best;
        // Fallback to any eN that looks like a percent (0..300)
        return pickJoinedCandidate(values, nums -> {
            if (nums.isEmpty()) return false; for (Double d : nums) { if (d <= 0 || d > 300) return false; } return true; });
    }

    /**
     * Parses every number (accepting commas or dots) from a combined string and returns them as doubles.
     */
    private java.util.List<Double> parseNumbers(String joined){
        java.util.List<Double> list = new java.util.ArrayList<>();
        if (joined == null || joined.isBlank()) return list;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+(?:[\\.,]\\d+)?)").matcher(joined);
        while (m.find()) {
            String s = m.group(1).replace(',', '.');
            try { list.add(Double.parseDouble(s)); } catch (NumberFormatException ignore) {}
        }
        return list;
    }

    /**
     * Normalizes Riot/CommunityDragon placeholder tokens by removing namespaces and lower-casing.
     */
    private String normalizeToken(String token) {
        if (token == null) return "";
        String t = token.trim().toLowerCase(Locale.ROOT);
        // Remove any namespace prefixes (e.g., spell.glacialstorm:slowamount -> slowamount)
        int colon = t.lastIndexOf(':');
        if (colon >= 0 && colon < t.length()-1) t = t.substring(colon+1);
        int dot = t.lastIndexOf('.');
        if (dot >= 0 && dot < t.length()-1) t = t.substring(dot+1);
        // common surrounding like totaldamage etc remain as-is
        return t;
    }

    /**
     * Detects whether a tooltip still looks unresolved (missing numbers, stray placeholders, etc.).
     */
    private boolean looksIncompleteTooltip(String text, String locale) {
        if (text == null) return true;
        String s = text.trim();
        if (s.isEmpty()) return true;
        String lc = s.toLowerCase(Locale.ROOT);
        // Unresolved placeholders
        if (lc.contains("{{") || lc.contains("}}") || lc.indexOf('@') >= 0) return true;
        // No digits at all -> likely missing numbers
        if (!lc.matches(".*\\d+.*")) return true;
        // Percent without preceding number
        if (lc.contains("%") && !lc.matches(".*\\d+(?:[\\s\\u00A0])*%.*")) return true;
        // "for seconds" or localized variants without a number just before the unit
        Pattern secEn = Pattern.compile("(?i)for(?:[\\s\\u00A0]+)(?:<[^>]+>(?:[\\s\\u00A0])*)*(?:second(?:s|\\(s\\))?|sec(?:\\.|onds?)?)");
        Pattern secDe = Pattern.compile("(?i)(?:<[^>]+>(?:[\\s\\u00A0])*)*sekunde(n)?");
        if (secEn.matcher(lc).find() && !lc.matches(".*\\d+(?:[\\s\\u00A0])*(?:second(?:s|\\(s\\))?|sec(?:\\.|onds?)?)(?:[\n\r\t\f ]|\\u00A0)*.*")) return true;
        if (secDe.matcher(lc).find() && !lc.matches(".*\\d+(,\\d+)?(?:[\\s\\u00A0])*sekunde(n)?(?:[\n\r\t\f ]|\\u00A0)*.*")) return true;
        return false;
    }

    /**
     * Formats coefficients for display (trim trailing zeros) so the UI shows "0.6" instead of "0.600000".
     */
    private String formatCoeff(double v) {
        // Display like 0.6 rather than 0.600000
        if (Math.abs(v - Math.rint(v)) < 1e-9) return String.valueOf((int)Math.rint(v));
        return String.format(Locale.ROOT, "%.2f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    /**
     * Converts Riot's link identifiers to human readable stat labels used in tooltips.
     */
    private String mapLinkToStatLabel(String link) {
        if (link == null) return "";
        switch (link) {
            case "spelldamage":
            case "AP":
                return "AP";
            case "attackdamage":
            case "AD":
                return "AD";
            case "armor":
                return "Armor";
            case "spelldamagebonus":
                return "AP";
            case "health":
                return "Health";
            case "bonusattackdamage":
                return "Bonus AD";
            default:
                return "";
        }
    }

    // ===== CommunityDragon fallback for fully-resolved dynamicDescription =====
    /**
     * Downloads CommunityDragon spell payloads and resolves their {@code @Token@} placeholders
     * to plain text. CommunityDragon often contains the fully substituted values even when
     * Data Dragon does not, making it a reliable fallback for missing numbers.
     */
    private List<String> fetchCDragonResolvedTooltips(int championKey, String ddragonLocale) throws IOException, InterruptedException {
        String loc = cdragonLocale(ddragonLocale);
        String url = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/" + loc + "/v1/champions/" + championKey + ".json";
        JsonNode node = getJson(url);
        if (node == null || node.isMissingNode()) return Collections.emptyList();
        JsonNode spells = node.path("spells");
        if (spells == null || !spells.isArray()) return Collections.emptyList();

        // Also fetch default-locale (en) as a robust fallback when localized strings miss numbers
        JsonNode defNode = null;
        JsonNode defSpells = null;
        try {
            String defUrl = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champions/" + championKey + ".json";
            defNode = getJson(defUrl);
            defSpells = defNode != null ? defNode.path("spells") : null;
        } catch (Exception ignore) {}

        // Build global value maps to resolve cross-references
        Map<String,String> globalValues = new HashMap<>();
        for (JsonNode sp : spells) {
            Map<String,String> per = extractCDragonSpellValues(sp);
            per.forEach((k,v) -> { if (v != null && !v.isBlank()) globalValues.putIfAbsent(k, v); });
        }
        Map<String,String> defGlobalValues = new HashMap<>();
        if (defSpells != null && defSpells.isArray()) {
            for (JsonNode sp : defSpells) {
                Map<String,String> per = extractCDragonSpellValues(sp);
                per.forEach((k,v) -> { if (v != null && !v.isBlank()) defGlobalValues.putIfAbsent(k, v); });
            }
        }

        List<String> tips = new ArrayList<>();
        for (int i = 0; i < spells.size(); i++) {
            JsonNode sp = spells.get(i);
            String dyn = sp.path("dynamicDescription").asText("");
            if (dyn == null) dyn = "";
            Map<String,String> localValues = extractCDragonSpellValues(sp);
            // Prepare default spell values for per-token fallback
            Map<String,String> defValues = Collections.emptyMap();
            JsonNode defSp = (defSpells != null && i < defSpells.size()) ? defSpells.get(i) : null;
            if (defSp != null) {
                defValues = extractCDragonSpellValues(defSp);
            }
            // Replace @Token@ occurrences with resolved values
            Pattern p = Pattern.compile("@([A-Za-z0-9_.:]+)@");
            Matcher m = p.matcher(dyn);
            StringBuffer sb = new StringBuffer();
            boolean anyTokenMissing = false;
            while (m.find()) {
                String token = m.group(1);
                String norm = normalizeToken(token);
                String rep = localValues.get(norm);
                if (rep == null || rep.isBlank()) rep = globalValues.getOrDefault(norm, "");
                if ((rep == null || rep.isBlank()) && defValues != null) rep = defValues.get(norm);
                if ((rep == null || rep.isBlank()) && defGlobalValues != null) rep = defGlobalValues.getOrDefault(norm, "");
                // Fallback: try without trailing digits (e.g., stunduration1 -> stunduration)
                if ((rep == null || rep.isBlank()) && norm.length() > 1 && Character.isDigit(norm.charAt(norm.length()-1))) {
                    String trimmed = norm.replaceAll("\\d+$", "");
                    rep = localValues.get(trimmed);
                    if (rep == null || rep.isBlank()) rep = globalValues.getOrDefault(trimmed, "");
                    if ((rep == null || rep.isBlank()) && defValues != null) rep = defValues.get(trimmed);
                    if ((rep == null || rep.isBlank()) && defGlobalValues != null) rep = defGlobalValues.getOrDefault(trimmed, "");
                }
                if (rep == null) rep = "";
                if (rep.isBlank()) anyTokenMissing = true;
                m.appendReplacement(sb, Matcher.quoteReplacement(rep));
            }
            m.appendTail(sb);
            String resolved = sb.toString();
            boolean hasDigits = resolved.matches(".*\\d+.*");

            // If still obviously incomplete (no digits at all), fall back to default dynamic text fully
            if ((!hasDigits || anyTokenMissing) && defSp != null) {
                String dynDef = defSp.path("dynamicDescription").asText("");
                Matcher md = p.matcher(dynDef != null ? dynDef : "");
                StringBuffer sbd = new StringBuffer();
                while (md.find()) {
                    String token = md.group(1);
                    String norm = normalizeToken(token);
                    String rep = defValues.get(norm);
                    if (rep == null || rep.isBlank()) rep = defGlobalValues.getOrDefault(norm, "");
                    // Fallback: try without trailing digits
                    if ((rep == null || rep.isBlank()) && norm.length() > 1 && Character.isDigit(norm.charAt(norm.length()-1))) {
                        String trimmed = norm.replaceAll("\\d+$", "");
                        rep = defValues.get(trimmed);
                        if (rep == null || rep.isBlank()) rep = defGlobalValues.getOrDefault(trimmed, "");
                    }
                    if (rep == null) rep = "";
                    md.appendReplacement(sbd, Matcher.quoteReplacement(rep));
                }
                md.appendTail(sbd);
                String resolvedDef = sbd.toString();
                if (resolvedDef.indexOf('@') >= 0) {
                    resolvedDef = resolvedDef.replace("@", "");
                }
                if (resolvedDef.matches(".*\\d+.*")) {
                    resolved = resolvedDef; // prefer numeric default text over incomplete localized one
                }
            }

            // In rare cases tokens might be unresolved; strip any stray '@'
            if (resolved.indexOf('@') >= 0) {
                resolved = resolved.replace("@", "");
            }
            // Heuristic patching for missing numbers (e.g., "for seconds") using combined value maps
            Map<String,String> mergedVals = new HashMap<>();
            if (localValues != null) mergedVals.putAll(localValues);
            if (globalValues != null) mergedVals.putAll(globalValues);
            if (defValues != null) mergedVals.putAll(defValues);
            if (defGlobalValues != null) mergedVals.putAll(defGlobalValues);
            resolved = patchMissingNumbers(resolved, mergedVals);
            tips.add(resolved);
        }
        return tips;
    }

    /**
     * Extracts the numerous token/value pairs from a CommunityDragon spell definition so that
     * dynamic descriptions can be rendered without hitting the remote API multiple times.
     */
    private Map<String,String> extractCDragonSpellValues(JsonNode spell) {
        Map<String,String> map = new HashMap<>();
        if (spell == null) return map;
        // Typical location: spellDataValues: [ { name: "SlowAmount", values: [..] }, ... ]
        JsonNode sdv = spell.path("spellDataValues");
        if (sdv != null && sdv.isArray()) {
            for (JsonNode v : sdv) {
                String name = v.path("name").asText("");
                if (!name.isBlank()) {
                    String val = joinNumberArray(v.path("values"));
                    if (val.isBlank()) val = joinNumberArray(v.path("valuesPerLevel"));
                    if (!val.isBlank()) map.put(name.trim().toLowerCase(Locale.ROOT), val);
                }
            }
        }
        // Sometimes nested under dataValues or mDataValues
        addNameValues(spell.path("dataValues"), map);
        addNameValues(spell.path("mDataValues"), map);

        // Also expose cost/cooldown if useful
        String cd = spell.path("cooldownCoefficients").isArray() ? joinNumberArray(spell.path("cooldownCoefficients")) : spell.path("cooldown").asText("");
        if (cd != null && !cd.isBlank()) map.put("cooldown", cd);
        String cost = spell.path("costCoefficients").isArray() ? joinNumberArray(spell.path("costCoefficients")) : spell.path("cost").asText("");
        if (cost != null && !cost.isBlank()) map.put("cost", cost);
        return map;
    }

    
    
    /**
     * Returns a map between champion numeric keys and their square splash URLs for the given locale.
     * Useful for client-side lookups when only the numeric key is known (e.g., match histories).
     */
    public Map<Integer, String> getChampionKeyToSquareUrl(Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        return self().getChampionKeyToSquareUrlCached(ver, loc);
    }

    
    /**
     * Cache-backed variant of {@link #getChampionKeyToSquareUrl(Locale)} that prevents redundant
     * parsing of the heavyweight {@code champion.json} payload.
     */
    @Cacheable(cacheNames = "ddragonChampionList", key = "'keyToImg|' + #version + '|' + #localeTag")
    public Map<Integer, String> getChampionKeyToSquareUrlCached(String version, String localeTag) throws IOException, InterruptedException {
        String url = DDRAGON_BASE + "/cdn/" + version + "/data/" + localeTag + "/champion.json";
        JsonNode root = getJson(url);
        JsonNode data = root.path("data");
        if (!data.isObject()) return Collections.emptyMap();
        String base = DDRAGON_BASE + "/cdn/" + version + "/img/champion/";
        Map<Integer,String> map = new java.util.HashMap<>();
        data.fields().forEachRemaining(e -> {
            JsonNode n = e.getValue();
            String keyStr = n.path("key").asText("");
            String img = n.path("image").path("full").asText("");
            if (!keyStr.isBlank() && !img.isBlank()) {
                try {
                    int k = Integer.parseInt(keyStr);
                    map.put(k, base + img);
                } catch (NumberFormatException ignore) {}
            }
        });
        return map;
    }

    /**
     * Adds "name" + "values" style entries from nested CommunityDragon structures to a map.
     */
    private void addNameValues(JsonNode node, Map<String,String> out) {
        if (node == null) return;
        if (node.isArray()) {
            for (JsonNode v : node) {
                String name = v.path("name").asText("");
                if (!name.isBlank()) {
                    String val = joinNumberArray(v.path("values"));
                    if (val.isBlank()) val = joinNumberArray(v.path("valuesPerLevel"));
                    if (!val.isBlank()) out.put(name.trim().toLowerCase(Locale.ROOT), val);
                }
            }
        } else if (node.isObject()) {
            node.fields().forEachRemaining(e -> {
                String key = e.getKey();
                JsonNode v = e.getValue();
                if (v.isArray()) {
                    String val = joinNumberArray(v);
                    if (!val.isBlank()) out.put(key.trim().toLowerCase(Locale.ROOT), val);
                } else if (v.isObject()) {
                    String name = v.path("name").asText("");
                    String outKey = !name.isBlank() ? name : key;
                    String val = joinNumberArray(v.path("values"));
                    if (val.isBlank()) val = joinNumberArray(v.path("valuesPerLevel"));
                    if (val.isBlank()) val = joinNumberArray(v.path("coefficients"));
                    if (val.isBlank()) val = joinNumberArray(v.path("mValue"));
                    if (val.isBlank()) val = joinNumberArray(v.path("value"));
                    if (val.isBlank() && v.isNumber()) val = joinNumberArray(v);
                    if (!val.isBlank()) out.put(outKey.trim().toLowerCase(Locale.ROOT), val);
                } else if (v.isNumber() || v.isTextual()) {
                    String val = joinNumberArray(v);
                    if (!val.isBlank()) out.put(key.trim().toLowerCase(Locale.ROOT), val);
                }
            });
        }
    }

    /**
     * Utility that turns a numeric/textual JSON array into a slash-separated string (e.g., "10/20/30").
     */
    private String joinNumberArray(JsonNode arr) {
        if (arr == null) return "";
        if (arr.isArray()) {
            List<String> parts = new ArrayList<>();
            for (JsonNode n : arr) {
                if (n.isNumber()) parts.add(formatCoeff(n.asDouble()));
                else if (n.isTextual()) parts.add(n.asText());
            }
            return parts.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("/"));
        } else if (arr.isNumber()) {
            return formatCoeff(arr.asDouble());
        } else if (arr.isTextual()) {
            return arr.asText();
        }
        return "";
    }

    /**
     * Converts Data Dragon locale values to the lowercase CommunityDragon variant.
     */
    private String cdragonLocale(String ddragonLocale) {
        if (ddragonLocale == null || ddragonLocale.isBlank()) return "en_us";
        // Convert e.g., de_DE -> de_de
        return ddragonLocale.toLowerCase(Locale.ROOT).replace('-', '_');
    }

    
    /**
     * Applies hand-crafted tooltip fixes for champions whose data is notoriously incomplete
     * (for example, Anivia or localized Amumu tooltips). The overrides are language aware and
     * therefore only touch tooltips for the relevant locales.
     */
    private void applyChampionSpecificTooltipOverrides(String championId, String ddragonLocale, List<SpellSummary> spells) {
        if (spells == null || spells.isEmpty()) return;
        String id = championId != null ? championId : "";
        String loc = ddragonLocale != null ? ddragonLocale : defaultLocale;
        boolean isGerman = loc.toLowerCase(Locale.ROOT).startsWith("de");
        boolean isEnglish = loc.toLowerCase(Locale.ROOT).startsWith("en");

        // Anivia: fill well-known numeric values if still missing
        if ("Anivia".equalsIgnoreCase(id) || "34".equals(id)) {
            // Q, W, E, R order
            if (spells.size() >= 4) {
                SpellSummary q = spells.get(0);
                SpellSummary w = spells.get(1);
                SpellSummary e = spells.get(2);
                SpellSummary r = spells.get(3);

                // Only override if no numbers present
                if (q.getTooltip() == null || !q.getTooltip().matches(".*\\d+.*")) {
                    if (isGerman) {
                        q.setTooltip("Anivia sendet einen massiven Eisbrocken aus, der Gegnern <magicDamage>60/85/110/135/160 (+45% AP) magischen Schaden</magicDamage> zufügt, sie 3 Sekunden lang <keywordMajor>unterkühlt</keywordMajor> und um 20/30/40 % <status>verlangsamt</status>. Am Ende seiner Reichweite detoniert der Eisbrocken, <status>betäubt</status> Gegner 1.1/1.2/1.3/1.4/1.5 Sekunden lang und verursacht zusätzlich <magicDamage>60/85/110/135/160 (+45% AP) magischen Schaden</magicDamage>.<br><br>Anivia kann die Fähigkeit <recast>reaktivieren</recast>, um frühzeitig zu detonieren.");
                    } else if (isEnglish) {
                        q.setTooltip("Anivia fires a massive chunk of ice, dealing <magicDamage>60/85/110/135/160 (+45% AP) magic damage</magicDamage>, <keywordMajor>Chilling</keywordMajor> enemies for 3 seconds and <status>Slowing</status> them by 20/30/40%. At max range the ice detonates, <status>Stunning</status> for 1.1/1.2/1.3/1.4/1.5 seconds and dealing an additional <magicDamage>60/85/110/135/160 (+45% AP) magic damage</magicDamage>.<br><br>Can be <recast>recast</recast> to detonate early.");
                    }
                }
                if (w.getTooltip() == null || !w.getTooltip().matches(".*\\d+.*")) {
                    if (isGerman) {
                        w.setTooltip("Anivia beschwört eine Eiswand herauf, die <b>5</b> Sekunden lang besteht und <b>400/500/600/700/800</b> Einheiten breit ist.");
                    } else if (isEnglish) {
                        w.setTooltip("Anivia summons a wall of ice that lasts <b>5</b> seconds and is <b>400/500/600/700/800</b> units wide.");
                    }
                }
                if (e.getTooltip() == null || !e.getTooltip().matches(".*\\d+.*")) {
                    if (isGerman) {
                        e.setTooltip("Anivia entfesselt einen frostigen Windstoß und verursacht <magicDamage>60/90/120/150/180 (+60% AP) magischen Schaden</magicDamage>. Gegen <keywordMajor>unterkühlte</keywordMajor> Ziele verursacht sie stattdessen <magicDamage>120/180/240/300/360 (+120% AP) magischen Schaden</magicDamage>.");
                    } else if (isEnglish) {
                        e.setTooltip("Anivia blasts a freezing gust, dealing <magicDamage>60/90/120/150/180 (+60% AP) magic damage</magicDamage>. Against <keywordMajor>Chilled</keywordMajor> targets, she instead deals <magicDamage>120/180/240/300/360 (+120% AP) magic damage</magicDamage>.");
                    }
                }
                if (r.getTooltip() == null || !r.getTooltip().matches(".*\\d+.*")) {
                    if (isGerman) {
                        r.setTooltip("<toggle>Aktivierbar:</toggle> Beschwört einen Eissturm, der Gegner um <b>20/30/40&nbsp;%</b> <status>verlangsamt</status> und <magicDamage><b>40/60/80</b> magischen Schaden pro Sekunde</magicDamage> verursacht. Der Sturm wächst über <b>1.5</b> Sekunden.<br><br>Voll ausgebildet <keywordMajor>unterkühlt</keywordMajor> der Sturm, <status>verlangsamt</status> um <b>40/50/60&nbsp;%</b> und verursacht <magicDamage><b>80/120/160</b> magischen Schaden pro Sekunde</magicDamage>. Kosten: <b>60</b> + <b>40/50/60</b> Mana pro Sekunde.");
                    } else if (isEnglish) {
                        r.setTooltip("<toggle>Toggle:</toggle> Calls a storm that <status>Slows</status> by <b>20/30/40%</b> and deals <magicDamage><b>40/60/80</b> magic damage per second</magicDamage>, growing over <b>1.5</b> seconds.<br><br>When fully formed, it <keywordMajor>Chills</keywordMajor>, <status>Slows</status> by <b>40/50/60%</b> and deals <magicDamage><b>80/120/160</b> magic damage per second</magicDamage>. Cost: <b>60</b> + <b>40/50/60</b> mana per second.");
                    }
                }
            }
        }

        // Amumu (German): ensure stun seconds are present and well-formed
        if (("Amumu".equalsIgnoreCase(id) || "32".equals(id)) && isGerman) {
            if (spells.size() >= 1) {
                SpellSummary q = spells.get(0); // Bandagenwurf
                if (q != null) {
                    String tip = q.getTooltip() == null ? "" : q.getTooltip();
                    // Robust: collapse any number/tag sequence before 'Sekunden' to exactly '1.1 Sekunden'
                    tip = tip.replaceAll("(?is)(?:\\d+(?:[\\.,]\\d+)?(?:[\\s\\u00A0]+|<[^>]+>)+)+sekunde(n)?", "1.1 Sekunden");
                    // Fallback: if 'Sekunden' present without number immediately before, enforce 1.1
                    if (tip.toLowerCase(Locale.ROOT).contains("sekunden") && !tip.matches(".*\\d+(?:[\\.,]\\d+)?(?:[\\s\\u00A0])*(?:<[^>]+>(?:[\\s\\u00A0])*)*sekunde(n)?.*")) {
                        tip = tip.replaceFirst("(?i)(?:<[^>]+>(?:[\\s\\u00A0])*)*sekunde(n)?", "1.1 Sekunden");
                    }
                    q.setTooltip(tip.trim());
                }
            }
            if (spells.size() >= 4) {
                SpellSummary r = spells.get(3); // Der Fluch der traurigen Mumie
                if (r != null) {
                    String tip = r.getTooltip() == null ? "" : r.getTooltip();
                    tip = tip.replaceAll("(?i)\\b(\\d)\\s+(\\d)\\s*sekunden\\b", "$1.$2 Sekunden");
                    if (tip.toLowerCase(Locale.ROOT).contains("sekunden") && !tip.matches(".*\\d+(?:[\\.,]\\d+)?\\s*sekunde(n)?.*")) {
                        tip = tip.replaceFirst("(?i)sekunde(n)?", "1,5/1,75/2 Sekunden");
                    }
                    r.setTooltip(tip.trim());
                }
            }
        }
    }

    
    /**
     * Looks up the numeric champion key for the provided textual identifier.
     */
    public Integer getChampionKey(String championId, Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        return self().getChampionKeyCached(championId, ver, loc);
    }

    
    /**
     * Cache-aware helper that fetches a champion's numeric key while respecting locale-specific
     * canonicalization rules.
     */
    @Cacheable(cacheNames = "ddragonChampionDetail", key = "'key-' + T(com.zerox80.riotapi.service.DataDragonService).safeLower(#championId) + '|' + #version + '|' + #localeTag")
    public Integer getChampionKeyCached(String championId, String version, String localeTag) throws IOException, InterruptedException {
        String cid = sanitizeChampionIdBasic(championId);
        if (cid == null) return null;
        String canonicalId = resolveCanonicalChampionId(cid, version, localeTag);
        if (canonicalId != null) {
            cid = canonicalId;
        }
        JsonNode root = getJson(DDRAGON_BASE + "/cdn/" + version + "/data/" + localeTag + "/champion/" + cid + ".json");
        JsonNode data = root.path("data").path(cid);
        if (data.isMissingNode()) {
            return null;
        }
        String keyStr = data.path("key").asText(null);
        if (keyStr == null || keyStr.isBlank()) return null;
        try {
            return Integer.parseInt(keyStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Executes an HTTP GET request (preferring HTTP/2) and parses the response body as JSON.
     * When the primary client fails (e.g., HTTP/2 not supported), the method retries once with
     * the fallback HTTP/1.1 client before surfacing an {@link IOException}.
     */
    private JsonNode getJson(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .header("Accept", "application/json")
                .header("User-Agent", this.userAgent)
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
        HttpResponse<String> res2 = fallbackHttp1.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res2.statusCode() / 100 != 2) {
            throw new IOException("HTTP " + res2.statusCode() + " from " + url);
        }
        return mapper.readTree(res2.body());
    }

    
    /**
     * Returns a map containing base CDN URLs (versioned image roots, CDN host, etc.) that
     * callers can use to construct sprite URLs without duplicating string concatenation logic.
     */
    @Cacheable(cacheNames = "ddragonImageBases", key = "#version == null || #version.isBlank() ? 'latest' : #version")
    public Map<String, String> getImageBases(String version) {
        String requested = version;
        boolean needsResolve = requested == null || requested.isBlank() || "latest".equalsIgnoreCase(requested);
        String ver;
        if (needsResolve) {
            String fallback = lastKnownVersion;
            if (fallback == null || fallback.isBlank()) {
                fallback = getLatestVersion();
            }
            ver = (fallback == null || fallback.isBlank()) ? getLatestVersion() : fallback;
        } else {
            ver = requested;
        }
        if (ver == null || ver.isBlank() || "latest".equalsIgnoreCase(ver)) {
            ver = getLatestVersion();
        }
        lastKnownVersion = ver;
        Map<String,String> map = new LinkedHashMap<>();
        map.put("version", ver);
        map.put("cdn", DDRAGON_BASE + "/cdn/");
        map.put("img", DDRAGON_BASE + "/cdn/" + ver + "/img/");
        map.put("champSquare", DDRAGON_BASE + "/cdn/" + ver + "/img/champion/");
        map.put("item", DDRAGON_BASE + "/cdn/" + ver + "/img/item/");
        map.put("spell", DDRAGON_BASE + "/cdn/" + ver + "/img/spell/");
        map.put("passive", DDRAGON_BASE + "/cdn/" + ver + "/img/passive/");
        map.put("splash", DDRAGON_BASE + "/cdn/img/champion/splash/");
        // Ranked emblems from CommunityDragon static assets (CDN-only)
        map.put("rankedMiniCrest", "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-static-assets/global/default/images/ranked-mini-crests/");
        map.put("rankedEmblem", "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-static-assets/global/default/images/ranked-emblem/");
        return map;
    }

    
    public Map<Integer, com.zerox80.riotapi.model.ItemSummary> getItems(Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        return self().getItemsCached(ver, loc);
    }

    
    @Cacheable(cacheNames = "ddragonItems", key = "#version + '|' + #localeTag")
    public Map<Integer, com.zerox80.riotapi.model.ItemSummary> getItemsCached(String version, String localeTag) throws IOException, InterruptedException {
        String url = DDRAGON_BASE + "/cdn/" + version + "/data/" + localeTag + "/item.json";
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

    
    public Map<Integer, String> getRuneStyleNames(Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        return self().getRuneStyleNamesCached(ver, loc);
    }

    
    @Cacheable(cacheNames = "ddragonRunes", key = "#version + '|' + #localeTag")
    public Map<Integer, String> getRuneStyleNamesCached(String version, String localeTag) throws IOException, InterruptedException {
        Map<Integer, String> styleNames = new java.util.HashMap<>();
        for (JsonNode style : getRunesArray(version, localeTag)) {
            int id = style.path("id").asInt();
            String name = style.path("name").asText("");
            styleNames.put(id, name);
        }
        return styleNames;
    }

    
    public Map<Integer, com.zerox80.riotapi.model.RunePerkInfo> getRunePerkLookup(Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        return self().getRunePerkLookupCached(ver, loc);
    }

    
    @Cacheable(cacheNames = "ddragonRunes", key = "'perks-' + #version + '|' + #localeTag")
    public Map<Integer, com.zerox80.riotapi.model.RunePerkInfo> getRunePerkLookupCached(String version, String localeTag) throws IOException, InterruptedException {
        Map<Integer, com.zerox80.riotapi.model.RunePerkInfo> map = new java.util.HashMap<>();
        for (JsonNode style : getRunesArray(version, localeTag)) {
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

    private JsonNode getRunesArray(String version, String localeTag) throws IOException, InterruptedException {
        String url = DDRAGON_BASE + "/cdn/" + version + "/data/" + localeTag + "/runesReforged.json";
        return getJson(url);
    }

    
    public Map<Integer, com.zerox80.riotapi.model.SummonerSpellInfo> getSummonerSpells(Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        return self().getSummonerSpellsCached(ver, loc);
    }

    
    @Cacheable(cacheNames = "ddragonSummonerSpells", key = "#version + '|' + #localeTag")
    public Map<Integer, com.zerox80.riotapi.model.SummonerSpellInfo> getSummonerSpellsCached(String version, String localeTag) throws IOException, InterruptedException {
        String url = DDRAGON_BASE + "/cdn/" + version + "/data/" + localeTag + "/summoner.json";
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

    
    /**
     * Null-safe helper used inside cache keys to normalize champion identifiers.
     */
    public static String safeLower(String value) {
        return value == null ? null : value.toLowerCase(Locale.ROOT);
    }
}
