// Package-Deklaration: Definiert dass diese Klasse zum Client-Package gehört
package com.zerox80.riotapi.client;

// Import für Jackson JSON-Parsing Exceptions
import com.fasterxml.jackson.core.JsonProcessingException;
// Import für Jackson um generische Typen zu deserialisieren (z.B. List<LeagueEntryDTO>)
import com.fasterxml.jackson.core.type.TypeReference;
// Import für Konfiguration des Deserialisierungsverhaltens
import com.fasterxml.jackson.databind.DeserializationFeature;
// Import der Hauptklasse für JSON Serialisierung/Deserialisierung
import com.fasterxml.jackson.databind.ObjectMapper;
// Import für Konfiguration der Property-Namensgebung (camelCase, snake_case, etc.)
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
// Import unserer Model-Klasse für Account-Daten von Riot
import com.zerox80.riotapi.model.AccountDto;
// Import für Ranked League Einträge (Rang, Division, LP)
import com.zerox80.riotapi.model.LeagueEntryDTO;
// Import für detaillierte Match-Daten aus der V5 API
import com.zerox80.riotapi.model.MatchV5Dto;
// Import für Summoner-Grunddaten (Level, Name, Icon)
import com.zerox80.riotapi.model.Summoner;
// Import für das Logging-Interface von SLF4J
import org.slf4j.Logger;
// Import für die Factory zum Erstellen von Logger-Instanzen
import org.slf4j.LoggerFactory;
// Import für automatische Dependency Injection durch Spring
import org.springframework.beans.factory.annotation.Autowired;
// Import um Werte aus application.properties zu injizieren
import org.springframework.beans.factory.annotation.Value;
// Import für direkten Cache-Zugriff zum manuellen Evicting
import org.springframework.cache.Cache;
// Import der Annotation um Methoden-Ergebnisse zu cachen
import org.springframework.cache.annotation.Cacheable;
// Import für Zugriff auf alle konfigurierten Caches
import org.springframework.cache.CacheManager;
// Import um diese Klasse als Spring-Bean zu registrieren
import org.springframework.stereotype.Component;
// Import für Metrics-Registry zur Performance-Überwachung
import io.micrometer.core.instrument.MeterRegistry;
// Import für Timer-Metriken um Latenz zu messen
import io.micrometer.core.instrument.Timer;

// Import für allgemeine IO-Exceptions
import java.io.IOException;
// Import für URI-Konstruktion (URLs für API-Calls)
import java.net.URI;
// Import zum URL-Encoding von Parametern (verhindert Injection)
import java.net.URLEncoder;
// Import des modernen Java HTTP Clients (seit Java 11)
import java.net.http.HttpClient;
// Import für HTTP Request Builder
import java.net.http.HttpRequest;
// Import für HTTP Response Objekte
import java.net.http.HttpResponse;
// Import für UTF-8 Encoding Standard
import java.nio.charset.StandardCharsets;
// Import für Zeitdauern (Timeouts, Delays)
import java.time.Duration;
// Import für Zeitpunkte mit Zeitzone (für Retry-After Header)
import java.time.ZonedDateTime;
// Import für Formatierung von Datums-Strings
import java.time.format.DateTimeFormatter;
// Import für Listen-Datenstruktur
import java.util.List;
// Import für Locale (Spracheinstellungen, hier für toLowerCase)
import java.util.Locale;
// Import für Optional Pattern (vermeidet null-Checks)
import java.util.Optional;
// Import für Map-Interface
import java.util.Map;
// Import für thread-sichere Hash-Map (wichtig für Concurrency)
import java.util.concurrent.ConcurrentHashMap;
// Import für Zeiteinheiten in Concurrent-Operationen
import java.util.concurrent.TimeUnit;
// Import für thread-sicheren Zufallsgenerator (für Jitter in Retry-Logik)
import java.util.concurrent.ThreadLocalRandom;
// Import für asynchrone Operationen mit Callbacks
import java.util.concurrent.CompletableFuture;
// Import für Supplier-Functional-Interface
import java.util.function.Supplier;
// Import für Semaphore zur Begrenzung gleichzeitiger Requests
import java.util.concurrent.Semaphore;
// Import für thread-sicheren Integer-Counter
import java.util.concurrent.atomic.AtomicInteger;


// @Component: Markiert diese Klasse als Spring-Bean die automatisch instanziiert wird
@Component
// Öffentliche Klasse: Haupt-Client für alle Kommunikation mit der Riot Games API
public class RiotApiClient {

    // Kommentar: Semaphore begrenzt die Anzahl gleichzeitiger ausgehender HTTP-Requests
    // Verhindert Überlastung der Riot API und das Erreichen von Rate Limits
    // final: Wert kann nach Initialisierung nicht mehr geändert werden
    private final Semaphore outboundLimiter;

    // static final: Klassenweite Konstante, Logger für diese spezifische Klasse
    // LoggerFactory.getLogger(): Erstellt einen Logger mit dem Klassennamen als Kategorie
    private static final Logger logger = LoggerFactory.getLogger(RiotApiClient.class);
    // final: Immutabler API-Key für Authentifizierung bei Riot
    private final String apiKey;
    // final: Platform-Region (z.B. "euw1" für Europa West)
    private final String platformRegion;
    // final: Regionales Routing für Match-History APIs (z.B. "europe")
    private final String regionalRoute;
    // final: Wiederverwendbarer HTTP-Client für alle Requests
    private final HttpClient httpClient;
    // final: JSON Mapper für Serialisierung/Deserialisierung
    private final ObjectMapper objectMapper;
    // final: Basis-URL für Community Dragon CDN (Profile Icons, etc.)
    private final String communityDragonUrl;
    // final: Registry für Metriken (Prometheus, Grafana)
    private final MeterRegistry meterRegistry;
    // final: Maximale Anzahl gleichzeitiger ausgehender Requests
    private final int maxConcurrentOutbound;
    // final: User-Agent String für HTTP-Headers (identifiziert unsere App)
    private final String userAgent;
    // final: Manager für alle Cache-Instanzen in der Anwendung
    private final CacheManager cacheManager;

    // Kommentar: In-flight Request Coalescing Maps - verhindert doppelte API-Calls
    // wenn mehrere Threads gleichzeitig die gleichen Daten anfordern (Cache-Miss)
    // ConcurrentHashMap: Thread-sichere Map für parallele Zugriffe
    // Key: Account-Identifier, Value: Future mit dem Ergebnis
    private final Map<String, CompletableFuture<AccountDto>> accountByRiotIdInFlight = new ConcurrentHashMap<>();
    // Map für in-flight Summoner-Requests nach PUUID
    private final Map<String, CompletableFuture<Summoner>> summonerByPuuidInFlight = new ConcurrentHashMap<>();
    // Map für in-flight League-Requests nach Summoner-ID
    private final Map<String, CompletableFuture<List<LeagueEntryDTO>>> leagueBySummonerIdInFlight = new ConcurrentHashMap<>();
    // Map für in-flight League-Requests nach PUUID
    private final Map<String, CompletableFuture<List<LeagueEntryDTO>>> leagueByPuuidInFlight = new ConcurrentHashMap<>();
    // Map für in-flight Match-ID Requests
    private final Map<String, CompletableFuture<List<String>>> matchIdsInFlight = new ConcurrentHashMap<>();
    // Map für in-flight Match-Detail Requests
    private final Map<String, CompletableFuture<MatchV5Dto>> matchDetailsInFlight = new ConcurrentHashMap<>();

    // static final: Typ-Token für Jackson um List<LeagueEntryDTO> zu deserialisieren
    // TypeReference: Erhält generische Typ-Information zur Laufzeit (Type Erasure Umgehung)
    // {} am Ende: Anonyme innere Klasse die TypeReference erweitert
    private static final TypeReference<List<LeagueEntryDTO>> LEAGUE_LIST_TYPE = new TypeReference<>() {};
    // Typ-Token für Deserialisierung von String-Listen (Match-IDs)
    private static final TypeReference<List<String>> MATCH_ID_LIST_TYPE = new TypeReference<>() {};

    
    @Autowired
    public RiotApiClient(@Value("${riot.api.key:}") String apiKey,
                         @Value("${riot.api.region:euw1}") String platformRegion,
                         @Value("${riot.api.community-dragon.url:https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/profile-icons}") String communityDragonUrl,
                         @Value("${app.user-agent:SummonerAPI/2.0 (github.com/zerox80/SummonerAPI)}") String userAgent,
                         ObjectMapper objectMapper,
                         MeterRegistry meterRegistry,
                         HttpClient httpClient,
                         @Value("${riot.api.max-concurrent:15}") int maxConcurrentOutbound,
                         CacheManager cacheManager) {
        this.apiKey = apiKey;
        this.platformRegion = platformRegion.toLowerCase(Locale.ROOT);
        this.regionalRoute = determineRegionalRoute(this.platformRegion);
        this.communityDragonUrl = communityDragonUrl;
        this.userAgent = userAgent;
        this.meterRegistry = meterRegistry;
        this.maxConcurrentOutbound = maxConcurrentOutbound > 0 ? maxConcurrentOutbound : 15;
        // Copy and harden the mapper: Riot APIs use lowerCamelCase; ignore unknown fields
        this.objectMapper = objectMapper.copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        this.httpClient = httpClient;
        this.outboundLimiter = new Semaphore(this.maxConcurrentOutbound);
        this.cacheManager = cacheManager;

        if (this.apiKey == null || this.apiKey.isBlank() || "YOUR_API_KEY".equalsIgnoreCase(this.apiKey)) {
            logger.warn("Riot API key is missing or placeholder. Set property 'riot.api.key' or env 'RIOT_API_KEY'.");
        }
    }

    
    public String getProfileIconUrl(int iconId) {
        return communityDragonUrl + "/" + iconId + ".jpg";
    }

    
    private String determineRegionalRoute(String platform) {
        switch (platform) {
            case "euw1", "eun1", "tr1", "ru", "me1":
                return "europe";
            case "na1", "br1", "la1", "la2", "oc1":
                return "americas";
            case "kr", "jp1":
                return "asia";
            case "vn2", "ph2", "sg2", "th2", "tw2", "id1":
                return "sea";
            default:
                logger.warn("Warning: Unknown platform region '{}' for determining regional route. Defaulting to platform itself.", platform);
                return platform;
        }
    }

    
    private <T> CompletableFuture<T> sendApiRequestAsync(String url, Class<T> responseClass, String requestType) {
        return sendRequest(url, requestType)
                .thenApply(response -> parseResponse(response, responseClass, requestType, url));
    }

    
    private <T> CompletableFuture<T> sendApiRequestAsync(String url, TypeReference<T> typeReference, String requestType) {
        return sendRequest(url, requestType)
                .thenApply(response -> parseResponse(response, typeReference, requestType, url));
    }

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration BASE_BACKOFF = Duration.ofSeconds(2);

    
    private CompletableFuture<HttpResponse<String>> sendRequest(String url, String requestType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Riot-Token", this.apiKey)
                .header("Accept", "application/json")
                .header("User-Agent", this.userAgent)
                .timeout(Duration.ofSeconds(15))
                .build();
        Timer.Sample sample = Timer.start(meterRegistry);
        AtomicInteger retries = new AtomicInteger(0);
        return sendWithRetryInstrumented(request, requestType, url, 1, retries)
                .whenComplete((response, throwable) -> {
                    String statusTag;
                    if (throwable != null) {
                        statusTag = "error";
                    } else {
                        int status = response.statusCode();
                        if (status == 429) statusTag = "429";
                        else if (status >= 200 && status < 300) statusTag = "2xx";
                        else if (status >= 400 && status < 500) statusTag = "4xx";
                        else if (status >= 500) statusTag = "5xx";
                        else statusTag = String.valueOf(status);
                    }
                    meterRegistry.counter("riotapi.client.requests", "type", requestType, "status", statusTag).increment();
                    Timer timer = meterRegistry.timer("riotapi.client.latency", "type", requestType, "status", statusTag, "retries", String.valueOf(retries.get()));
                    sample.stop(timer);
                });
    }

    
    private CompletableFuture<HttpResponse<String>> sendRequestWithBearer(String url, String requestType, String bearerToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + bearerToken)
                .header("Accept", "application/json")
                .header("User-Agent", this.userAgent)
                .timeout(Duration.ofSeconds(15))
                .build();
        Timer.Sample sample = Timer.start(meterRegistry);
        AtomicInteger retries = new AtomicInteger(0);
        return sendWithRetryInstrumented(request, requestType, url, 1, retries)
                .whenComplete((response, throwable) -> {
                    String statusTag;
                    if (throwable != null) {
                        statusTag = "error";
                    } else {
                        int status = response.statusCode();
                        if (status == 429) statusTag = "429";
                        else if (status >= 200 && status < 300) statusTag = "2xx";
                        else if (status >= 400 && status < 500) statusTag = "4xx";
                        else if (status >= 500) statusTag = "5xx";
                        else statusTag = String.valueOf(status);
                    }
                    meterRegistry.counter("riotapi.client.requests", "type", requestType, "status", statusTag).increment();
                    Timer timer = meterRegistry.timer("riotapi.client.latency", "type", requestType, "status", statusTag, "retries", String.valueOf(retries.get()));
                    sample.stop(timer);
                });
    }

    
    private CompletableFuture<HttpResponse<String>> sendWithRetryInstrumented(HttpRequest request, String requestType, String url, int attempt, AtomicInteger retries) {
        return acquirePermitAsync()
                .thenCompose(v -> httpClient
                        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .handle((response, throwable) -> new Object[]{response, throwable})
                        .whenComplete((pair, t) -> outboundLimiter.release())
                )
                .thenCompose(pair -> {
                    @SuppressWarnings("unchecked")
                    HttpResponse<String> response = (HttpResponse<String>) pair[0];
                    Throwable throwable = (Throwable) pair[1];

                    if (throwable != null) {
                        if (attempt < MAX_ATTEMPTS) {
                            Duration delay = computeBackoffDelay(attempt, Optional.empty());
                            logger.warn("Request {} to {} failed (attempt {}/{})\n Retrying in {} ms. Cause: {}",
                                    requestType, url, attempt, MAX_ATTEMPTS, delay.toMillis(), throwable.toString());
                            retries.incrementAndGet();
                            meterRegistry.counter("riotapi.client.retries", "type", requestType).increment();
                            return delayed(delay).thenCompose(v -> sendWithRetryInstrumented(request, requestType, url, attempt + 1, retries));
                        }
                        return CompletableFuture.failedFuture(throwable);
                    }

                    int status = response.statusCode();
                    if ((status == 429 || (status >= 500 && status < 600)) && attempt < MAX_ATTEMPTS) {
                        Optional<Long> retryAfterSeconds = parseRetryAfterSeconds(response);
                        Duration delay = computeBackoffDelay(attempt, retryAfterSeconds);
                        logger.warn("Request {} to {} returned {}. Retrying in {} ms (attempt {}/{}).",
                                requestType, url, status, delay.toMillis(), attempt, MAX_ATTEMPTS);
                        retries.incrementAndGet();
                        meterRegistry.counter("riotapi.client.retries", "type", requestType).increment();
                        return delayed(delay).thenCompose(v -> sendWithRetryInstrumented(request, requestType, url, attempt + 1, retries));
                    }

                    return CompletableFuture.completedFuture(response);
                });
    }

    
    private CompletableFuture<Void> acquirePermitAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        retryAcquire(future);
        return future;
    }

    
    private void retryAcquire(CompletableFuture<Void> future) {
        if (outboundLimiter.tryAcquire()) {
            future.complete(null);
        } else {
            CompletableFuture.delayedExecutor(25, TimeUnit.MILLISECONDS)
                    .execute(() -> retryAcquire(future));
        }
    }

    
    private Optional<Long> parseRetryAfterSeconds(HttpResponse<String> response) {
        return response.headers().firstValue("Retry-After").flatMap(value -> {
            if (value == null) return Optional.empty();
            String raw = value.trim();
            // Case 1: delta-seconds
            try {
                return Optional.of(Long.parseLong(raw));
            } catch (NumberFormatException ignored) {
                // Case 2: HTTP-date (RFC 7231) e.g., Sun, 06 Nov 1994 08:49:37 GMT
                try {
                    ZonedDateTime until = ZonedDateTime.parse(raw, DateTimeFormatter.RFC_1123_DATE_TIME);
                    long seconds = Duration.between(java.time.Instant.now(), until.toInstant()).getSeconds();
                    return Optional.of(Math.max(1, seconds));
                } catch (Exception ignored2) {
                    return Optional.empty();
                }
            }
        });
    }

    
    private Duration computeBackoffDelay(int attempt, Optional<Long> retryAfterSeconds) {
        if (retryAfterSeconds.isPresent()) {
            return Duration.ofSeconds(Math.max(1, retryAfterSeconds.get()));
        }
        long jitterMs = ThreadLocalRandom.current().nextLong(100, 400);
        long backoffMs = BASE_BACKOFF.toMillis() * (long) Math.pow(2, attempt - 1);
        return Duration.ofMillis(backoffMs + jitterMs);
    }

    
    private CompletableFuture<Void> delayed(Duration delay) {
        return CompletableFuture.supplyAsync(() -> null, CompletableFuture.delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS)).thenAccept(v -> {});
    }

    
    private <T> T parseResponse(HttpResponse<String> response, Class<T> responseClass, String requestType, String url) {
        if (response.statusCode() == 200) {
            try {
                return objectMapper.readValue(response.body(), responseClass);
            } catch (JsonProcessingException e) {
                throw new RiotApiRequestException("Failed to parse API response for " + requestType, e);
            }
        } else if (response.statusCode() == 404) {
            logger.warn("API Request ({}) to URL '{}' returned 404 Not Found.", requestType, url);
            return null;
        } else {
            String snippet = abbreviate(response.body(), 500);
            logger.error("API Request Failed ({}): status={} url={} bodySnippet={}", requestType, response.statusCode(), url, snippet);
            throw new RiotApiRequestException("API request (" + requestType + ") failed with status code: " + response.statusCode());
        }
    }

    
    private <T> T parseResponse(HttpResponse<String> response, TypeReference<T> typeReference, String requestType, String url) {
        if (response.statusCode() == 200) {
            try {
                return objectMapper.readValue(response.body(), typeReference);
            } catch (JsonProcessingException e) {
                throw new RiotApiRequestException("Failed to parse API response for " + requestType, e);
            }
        } else if (response.statusCode() == 404) {
            logger.warn("API Request ({}) to URL '{}' returned 404 Not Found.", requestType, url);
            return null;
        } else {
            String snippet = abbreviate(response.body(), 500);
            logger.error("API Request Failed ({}): status={} url={} bodySnippet={}", requestType, response.statusCode(), url, snippet);
            throw new RiotApiRequestException("API request (" + requestType + ") failed with status code: " + response.statusCode());
        }
    }

    
    @Cacheable(value = "accounts", key = "T(com.zerox80.riotapi.client.RiotApiClient).accountCacheKey(#gameName, #tagLine)")
    public CompletableFuture<AccountDto> getAccountByRiotId(String gameName, String tagLine) {
        String encodedGameName = URLEncoder.encode(gameName, StandardCharsets.UTF_8).replace("+", "%20");
        String encodedTagLine = URLEncoder.encode(tagLine, StandardCharsets.UTF_8);

        String host = this.regionalRoute + ".api.riotgames.com";
        String path = "/riot/account/v1/accounts/by-riot-id/" + encodedGameName + "/" + encodedTagLine;
        String url = "https://" + host + path;

        logger.debug(">>> RiotApiClient (Account): Requesting RAW Riot ID: [{}#{}]", gameName, tagLine);
        logger.debug(">>> RiotApiClient (Account): Requesting ENCODED URL: [{}]", url);

        String inflightKey = accountCacheKey(gameName, tagLine);
        CompletableFuture<AccountDto> future = coalesce(accountByRiotIdInFlight, inflightKey,
                () -> sendApiRequestAsync(url, AccountDto.class, "Account"));
        return evictOnException(future, "accounts", inflightKey);
    }

    
    @Cacheable(value = "summoners", key = "#puuid")
    public CompletableFuture<Summoner> getSummonerByPuuid(String puuid) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/summoner/v4/summoners/by-puuid/" + puuid;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (Summoner): Requesting by PUUID [{}]", maskPuuid(puuid));
        CompletableFuture<Summoner> future = coalesce(summonerByPuuidInFlight, puuid,
                () -> sendApiRequestAsync(url, Summoner.class, "Summoner"));
        return evictOnException(future, "summoners", puuid);
    }

    
    @Cacheable(value = "leagueEntries", key = "'sid:' + #summonerId")
    public CompletableFuture<List<LeagueEntryDTO>> getLeagueEntriesBySummonerId(String summonerId) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/league/v4/entries/by-summoner/" + summonerId;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (LeagueEntries): Requesting URL: [{}]", url);
        String cacheKey = "sid:" + summonerId;
        CompletableFuture<List<LeagueEntryDTO>> future = coalesce(leagueBySummonerIdInFlight, summonerId,
                () -> sendApiRequestAsync(url, LEAGUE_LIST_TYPE, "LeagueEntries")
                        .thenApply(list -> list != null ? list : List.of()));
        return evictOnException(future, "leagueEntries", cacheKey);
    }

    
    @Cacheable(value = "leagueEntries", key = "'puuid:' + #puuid")
    public CompletableFuture<List<LeagueEntryDTO>> getLeagueEntriesByPuuid(String puuid) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/league/v4/entries/by-puuid/" + puuid;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (LeagueEntries PUUID): Requesting by PUUID [{}]", maskPuuid(puuid));
        String cacheKey = "puuid:" + puuid;
        CompletableFuture<List<LeagueEntryDTO>> future = coalesce(leagueByPuuidInFlight, puuid,
                () -> sendApiRequestAsync(url, LEAGUE_LIST_TYPE, "LeagueEntriesByPuuid")
                        .thenApply(list -> list != null ? list : List.of()));
        return evictOnException(future, "leagueEntries", cacheKey);
    }

    
    public CompletableFuture<Summoner> getSummonerMeWithBearer(String bearerToken) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/summoner/v4/summoners/me";
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (SummonerMe RSO): Requesting URL: [{}]", url);
        return sendRequestWithBearer(url, "SummonerMeRSO", bearerToken)
                .thenApply(response -> parseResponse(response, Summoner.class, "SummonerMeRSO", url));
    }

    
    @Cacheable(value = "matchIds", key = "#puuid + '-' + #count")
    public CompletableFuture<List<String>> getMatchIdsByPuuid(String puuid, int count) {
        String host = this.regionalRoute + ".api.riotgames.com";
        String path = "/lol/match/v5/matches/by-puuid/" + puuid + "/ids?count=" + count;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (MatchIds): Requesting by PUUID [{}], count {}", maskPuuid(puuid), count);
        String key = puuid + "-" + count;
        CompletableFuture<List<String>> future = coalesce(matchIdsInFlight, key,
                () -> sendApiRequestAsync(url, MATCH_ID_LIST_TYPE, "MatchIds")
                        .thenApply(list -> list != null ? list : List.of()));
        return evictOnException(future, "matchIds", key);
    }

    
    @Cacheable(value = "matchIds", key = "#puuid + '-' + #start + '-' + #count")
    public CompletableFuture<List<String>> getMatchIdsByPuuid(String puuid, int start, int count) {
        String host = this.regionalRoute + ".api.riotgames.com";
        String path = "/lol/match/v5/matches/by-puuid/" + puuid + "/ids?start=" + start + "&count=" + count;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (MatchIdsPaged): PUUID [{}], start {}, count {}", maskPuuid(puuid), start, count);
        String key = puuid + "-" + start + "-" + count;
        CompletableFuture<List<String>> future = coalesce(matchIdsInFlight, key,
                () -> sendApiRequestAsync(url, MATCH_ID_LIST_TYPE, "MatchIdsPaged")
                        .thenApply(list -> list != null ? list : List.of()));
        return evictOnException(future, "matchIds", key);
    }

    
    @Cacheable(value = "matchDetails", key = "#matchId")
    public CompletableFuture<MatchV5Dto> getMatchDetails(String matchId) {
        String host = this.regionalRoute + ".api.riotgames.com";
        String path = "/lol/match/v5/matches/" + matchId;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (MatchDetails): Requesting URL: [{}]", url);
        CompletableFuture<MatchV5Dto> future = coalesce(matchDetailsInFlight, matchId,
                () -> sendApiRequestAsync(url, MatchV5Dto.class, "MatchDetails"));
        return evictOnException(future, "matchDetails", matchId);
    }

    
    @Cacheable(value = "leagueEntries", key = "#queue + '|' + #tier + '|' + #division + '|' + #page")
    public CompletableFuture<List<LeagueEntryDTO>> getEntriesByQueueTierDivision(String queue, String tier, String division, int page) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/league/v4/entries/" + urlEncode(queue) + "/" + urlEncode(tier) + "/" + urlEncode(division) + "?page=" + page;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (Entries {} {} {} p{}): {}", queue, tier, division, page, url);
        String cacheKey = queue + "|" + tier + "|" + division + "|" + page;
        CompletableFuture<List<LeagueEntryDTO>> future = sendApiRequestAsync(url, LEAGUE_LIST_TYPE, "LeagueEntriesByTier");
        return evictOnException(future, "leagueEntries", cacheKey);
    }

    
    private String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    
    @Cacheable(value = "summoners", key = "#summonerId")
    public CompletableFuture<Summoner> getSummonerById(String summonerId) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/summoner/v4/summoners/" + summonerId;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (Summoner by ID): Requesting ID [{}]", maskId(summonerId));
        CompletableFuture<Summoner> future = sendApiRequestAsync(url, Summoner.class, "SummonerById");
        return evictOnException(future, "summoners", summonerId);
    }

    
    public String getPlatformRegion() {
        return platformRegion;
    }

    
    private <K, T> CompletableFuture<T> coalesce(Map<K, CompletableFuture<T>> inFlightMap, K key, Supplier<CompletableFuture<T>> loader) {
        return inFlightMap.computeIfAbsent(key, k -> loader.get()
                .whenComplete((res, ex) -> inFlightMap.remove(k)));
    }

    
    private static String maskPuuid(String puuid) {
        if (puuid == null) return "(null)";
        int len = puuid.length();
        if (len <= 10) return "***";
        return puuid.substring(0, 6) + "..." + puuid.substring(len - 4);
    }

    
    private static String maskId(String id) {
        if (id == null) return "(null)";
        int len = id.length();
        if (len <= 8) return "***";
        return id.substring(0, Math.min(4, len)) + "..." + id.substring(len - Math.min(3, len));
    }

    
    private String abbreviate(String s, int max) {
        if (s == null) return null;
        if (s.length() <= max) return s;
        return s.substring(0, max) + "…";
    }

    
    private <T> CompletableFuture<T> evictOnException(CompletableFuture<T> future, String cacheName, Object cacheKey) {
        return future.whenComplete((value, throwable) -> {
            if (throwable != null && cacheManager != null) {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.evict(cacheKey);
                }
            }
        });
    }

    
    public static String lower(String value) {
        return java.util.Objects.requireNonNull(value, "value").toLowerCase(Locale.ROOT);
    }

    
    public static String accountCacheKey(String gameName, String tagLine) {
        return lower(gameName) + "#" + lower(tagLine);
    }
}
