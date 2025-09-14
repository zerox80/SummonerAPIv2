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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

@Service
public class DataDragonService {

    private static final String DDRAGON_BASE = "https://ddragon.leagueoflegends.com";
    private static final String DEFAULT_LOCALE = "en_US";

    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public DataDragonService(HttpClient riotApiHttpClient) {
        this.httpClient = riotApiHttpClient;
    }

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

        // Try to obtain champion numeric key for potential CommunityDragon fallback
        Integer champKey = null;
        String keyStrLocal = data.path("key").asText(null);
        if (keyStrLocal != null) {
            try { champKey = Integer.parseInt(keyStrLocal); } catch (NumberFormatException ignore) { champKey = null; }
        }
        if (champKey == null) {
            try { champKey = getChampionKey(cid, locale); } catch (Exception ignore) {}
        }
        List<String> cdragonTooltips = Collections.emptyList();
        if (champKey != null) {
            try {
                cdragonTooltips = fetchCDragonResolvedTooltips(champKey, loc);
            } catch (Exception ignore) {
                cdragonTooltips = Collections.emptyList();
            }
        }

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
        // Build a global map of values across all spells to resolve cross-spell references like spell.X:token
        Map<String,String> ddragonGlobalValues = new HashMap<>();
        if (spellsNode != null && spellsNode.isArray()) {
            for (JsonNode s : spellsNode) collectGlobalDDragonValues(s, ddragonGlobalValues);
        }
        if (spellsNode != null && spellsNode.isArray()) {
            for (JsonNode s : spellsNode) {
                String sid = s.path("id").asText("");
                String sname = s.path("name").asText("");
                String tooltip = null;
                // Prefer CommunityDragon dynamicDescription (already resolved tokens)
                int idx = spells.size();
                if (cdragonTooltips != null && idx < cdragonTooltips.size()) {
                    String cd = cdragonTooltips.get(idx);
                    if (cd != null && !cd.isBlank()) tooltip = cd;
                }
                // Fallback to DDragon tooltip if CDragon text looks unfilled (no digits)
                if (tooltip == null || !tooltip.matches(".*\\d+.*")) {
                    String dd = renderSpellTooltip(s, ddragonGlobalValues);
                    if (dd != null && !dd.isBlank()) tooltip = dd;
                }
                if (tooltip == null) tooltip = "";
                // Clean up extra whitespace from mixed sources
                tooltip = tooltip.replaceAll("\\s+", " ").trim();
                String sImg = s.path("image").path("full").asText("");
                spells.add(new SpellSummary(sid, sname, tooltip, sImg));
            }
        }
        // Champion-specific last-resort fixes for missing values
        applyChampionSpecificTooltipOverrides(id, loc, spells);
        return new ChampionDetail(id, name, title, lore, tags, imageFull, passive, spells);
    }

    /**
     * Best-effort render of a DDragon spell tooltip by replacing common placeholders
     * like {{ e1 }}, {{ cooldown }}, {{ cost }}, and keys present in datavalues/vars.
     * Unknown placeholders are removed to avoid leaking raw template tokens to the UI.
     */
    private String renderSpellTooltip(JsonNode spellNode) {
        return renderSpellTooltip(spellNode, Collections.emptyMap());
    }

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

        // Some tooltips contain special tags like <status>...</status>; keep as-is for utext
        return sb.toString();
    }

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

    private String formatCoeff(double v) {
        // Display like 0.6 rather than 0.600000
        if (Math.abs(v - Math.rint(v)) < 1e-9) return String.valueOf((int)Math.rint(v));
        return String.format(Locale.ROOT, "%.2f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

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
    private List<String> fetchCDragonResolvedTooltips(int championKey, String ddragonLocale) throws IOException, InterruptedException {
        String loc = cdragonLocale(ddragonLocale);
        String url = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/" + loc + "/v1/champions/" + championKey + ".json";
        JsonNode node = getJson(url);
        if (node == null || node.isMissingNode()) return Collections.emptyList();
        JsonNode spells = node.path("spells");
        if (spells == null || !spells.isArray()) return Collections.emptyList();
        // Build a global value map across all spells to resolve cross-references
        Map<String,String> globalValues = new HashMap<>();
        for (JsonNode sp : spells) {
            Map<String,String> per = extractCDragonSpellValues(sp);
            per.forEach((k,v) -> { if (v != null && !v.isBlank()) globalValues.putIfAbsent(k, v); });
        }
        // Some localized JSONs omit spellDataValues; merge in from default locale if empty
        if (globalValues.isEmpty()) {
            try {
                String defUrl = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champions/" + championKey + ".json";
                JsonNode defNode = getJson(defUrl);
                JsonNode defSpells = defNode != null ? defNode.path("spells") : null;
                if (defSpells != null && defSpells.isArray()) {
                    for (JsonNode sp : defSpells) {
                        Map<String,String> per = extractCDragonSpellValues(sp);
                        per.forEach((k,v) -> { if (v != null && !v.isBlank()) globalValues.putIfAbsent(k, v); });
                    }
                }
            } catch (Exception ignore) {}
        }

        List<String> tips = new ArrayList<>();
        for (JsonNode sp : spells) {
            String dyn = sp.path("dynamicDescription").asText("");
            if (dyn == null) dyn = "";
            Map<String,String> localValues = extractCDragonSpellValues(sp);
            // Replace @Token@ occurrences with resolved values (prefer local, then global)
            Pattern p = Pattern.compile("@([A-Za-z0-9_.:]+)@");
            Matcher m = p.matcher(dyn);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String token = m.group(1);
                String norm = normalizeToken(token);
                String rep = localValues.get(norm);
                if (rep == null || rep.isBlank()) rep = globalValues.getOrDefault(norm, "");
                m.appendReplacement(sb, Matcher.quoteReplacement(rep));
            }
            m.appendTail(sb);
            String resolved = sb.toString();
            // In rare cases tokens might be unresolved; strip any stray '@'
            if (resolved.indexOf('@') >= 0) {
                resolved = resolved.replace("@", "");
            }
            tips.add(resolved);
        }
        return tips;
    }

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
     * Returns a map from champion numeric key (e.g., 34) to full champion square image URL.
     * Uses the latest version and resolved locale.
     */
    @Cacheable(cacheNames = "ddragonChampionList", key = "'keyToImg|' + #root.target.getLatestVersion() + '|' + #root.target.resolveLocale(#locale)")
    public Map<Integer, String> getChampionKeyToSquareUrl(Locale locale) throws IOException, InterruptedException {
        String ver = getLatestVersion();
        String loc = resolveLocale(locale);
        String url = DDRAGON_BASE + "/cdn/" + ver + "/data/" + loc + "/champion.json";
        JsonNode root = getJson(url);
        JsonNode data = root.path("data");
        if (!data.isObject()) return Collections.emptyMap();
        String base = DDRAGON_BASE + "/cdn/" + ver + "/img/champion/";
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
                    if (!name.isBlank()) {
                        String val = joinNumberArray(v.path("values"));
                        if (val.isBlank()) val = joinNumberArray(v.path("valuesPerLevel"));
                        if (!val.isBlank()) out.put(name.trim().toLowerCase(Locale.ROOT), val);
                    }
                }
            });
        }
    }

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

    private String cdragonLocale(String ddragonLocale) {
        if (ddragonLocale == null || ddragonLocale.isBlank()) return "en_us";
        // Convert e.g., de_DE -> de_de
        return ddragonLocale.toLowerCase(Locale.ROOT).replace('-', '_');
    }

    /**
     * Last-resort hardcoded overrides for champions where neither CDragon nor DDragon
     * exposes numeric values in public static JSON. Only used when tooltips still lack digits.
     */
    private void applyChampionSpecificTooltipOverrides(String championId, String ddragonLocale, List<SpellSummary> spells) {
        if (spells == null || spells.isEmpty()) return;
        String id = championId != null ? championId : "";
        String loc = ddragonLocale != null ? ddragonLocale : DEFAULT_LOCALE;
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
