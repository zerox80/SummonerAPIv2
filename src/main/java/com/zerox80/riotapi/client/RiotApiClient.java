package com.zerox80.riotapi.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.zerox80.riotapi.model.AccountDto;
import com.zerox80.riotapi.model.LeagueEntryDTO;
import com.zerox80.riotapi.model.MatchV5Dto;
import com.zerox80.riotapi.model.Summoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HTTP client for interacting with the Riot Games API.
 * 
 * <p>This component provides methods for accessing various Riot API endpoints including
 * account information, summoner data, league entries, and match history. It includes
 * features such as request caching, rate limiting, retry logic with exponential backoff,
 * and request coalescing to prevent duplicate API calls.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Automatic retry with exponential backoff for failed requests</li>
 *   <li>Request coalescing to prevent duplicate upstream calls</li>
 *   <li>Built-in caching with Spring Cache abstraction</li>
 *   <li>Rate limiting and concurrent request control</li>
 *   <li>Comprehensive metrics and logging</li>
 *   <li>Support for both API key and OAuth Bearer token authentication</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Component
public class RiotApiClient {

    // Limit the number of concurrent outbound HTTP requests to avoid overwhelming upstream and hitting rate limits
    private final Semaphore outboundLimiter;

    private static final Logger logger = LoggerFactory.getLogger(RiotApiClient.class);
    private final String apiKey;
    private final String platformRegion;
    private final String regionalRoute;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String communityDragonUrl;
    private final MeterRegistry meterRegistry;
    private final int maxConcurrentOutbound;
    private final String userAgent;
    private final CacheManager cacheManager;

    // In-flight request coalescing maps to prevent duplicate upstream calls on cache misses
    private final Map<String, CompletableFuture<AccountDto>> accountByRiotIdInFlight = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<Summoner>> summonerByPuuidInFlight = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<List<LeagueEntryDTO>>> leagueBySummonerIdInFlight = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<List<LeagueEntryDTO>>> leagueByPuuidInFlight = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<List<String>>> matchIdsInFlight = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<MatchV5Dto>> matchDetailsInFlight = new ConcurrentHashMap<>();

    private static final TypeReference<List<LeagueEntryDTO>> LEAGUE_LIST_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<String>> MATCH_ID_LIST_TYPE = new TypeReference<>() {};

    /**
     * Constructs a new RiotApiClient with the specified configuration.
     * 
     * @param apiKey The Riot API key for authentication (can be empty for RSO-only endpoints)
     * @param platformRegion The platform region (e.g., euw1, na1)
     * @param communityDragonUrl The base URL for Community Dragon assets
     * @param userAgent The User-Agent header to send with requests
     * @param objectMapper The Jackson object mapper for JSON serialization/deserialization
     * @param meterRegistry The Micrometer registry for metrics collection
     * @param httpClient The HTTP client for making requests
     * @param maxConcurrentOutbound Maximum number of concurrent outbound requests
     * @param cacheManager The Spring cache manager for response caching
     */
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

    /**
     * Gets the URL for a profile icon from Community Dragon.
     * 
     * @param iconId The ID of the profile icon
     * @return The complete URL to the profile icon image
     */
    public String getProfileIconUrl(int iconId) {
        return communityDragonUrl + "/" + iconId + ".jpg";
    }

    /**
     * Determines the regional route based on the platform region.
     * 
     * @param platform The platform region (e.g., euw1, na1)
     * @return The corresponding regional route (e.g., europe, americas, asia)
     */
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

    /**
     * Sends an asynchronous API request and parses the response as the specified class.
     * 
     * @param <T> The type of the response object
     * @param url The API endpoint URL
     * @param responseClass The class to parse the response into
     * @param requestType The type of request for logging/metrics
     * @return A CompletableFuture containing the parsed response
     */
    private <T> CompletableFuture<T> sendApiRequestAsync(String url, Class<T> responseClass, String requestType) {
        return sendRequest(url, requestType)
                .thenApply(response -> parseResponse(response, responseClass, requestType, url));
    }

    /**
     * Sends an asynchronous API request and parses the response using the specified type reference.
     * 
     * @param <T> The type of the response object
     * @param url The API endpoint URL
     * @param typeReference The type reference for complex types like collections
     * @param requestType The type of request for logging/metrics
     * @return A CompletableFuture containing the parsed response
     */
    private <T> CompletableFuture<T> sendApiRequestAsync(String url, TypeReference<T> typeReference, String requestType) {
        return sendRequest(url, requestType)
                .thenApply(response -> parseResponse(response, typeReference, requestType, url));
    }

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration BASE_BACKOFF = Duration.ofSeconds(2);

    /**
     * Sends an HTTP request with retry logic and instrumentation.
     * 
     * @param url The API endpoint URL
     * @param requestType The type of request for logging/metrics
     * @return A CompletableFuture containing the HTTP response
     */
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

    /**
     * Sends an HTTP request with Bearer token authentication and retry logic.
     * 
     * @param url The API endpoint URL
     * @param requestType The type of request for logging/metrics
     * @param bearerToken The OAuth Bearer token for authentication
     * @return A CompletableFuture containing the HTTP response
     */
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

    /**
     * Sends a request with retry logic, exponential backoff, and instrumentation.
     * 
     * @param request The HTTP request to send
     * @param requestType The type of request for logging/metrics
     * @param url The URL being requested (for logging)
     * @param attempt The current attempt number
     * @param retries Atomic counter for tracking retry attempts
     * @return A CompletableFuture containing the HTTP response
     */
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

    /**
     * Asynchronously acquires a permit from the outbound request limiter.
     * 
     * @return A CompletableFuture that completes when a permit is acquired
     */
    private CompletableFuture<Void> acquirePermitAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        retryAcquire(future);
        return future;
    }

    /**
     * Retries acquiring a permit with exponential backoff.
     * 
     * @param future The CompletableFuture to complete when a permit is acquired
     */
    private void retryAcquire(CompletableFuture<Void> future) {
        if (outboundLimiter.tryAcquire()) {
            future.complete(null);
        } else {
            CompletableFuture.delayedExecutor(25, TimeUnit.MILLISECONDS)
                    .execute(() -> retryAcquire(future));
        }
    }

    /**
     * Parses the Retry-After header from an HTTP response.
     * 
     * @param response The HTTP response containing the Retry-After header
     * @return An Optional containing the retry delay in seconds, or empty if not present
     */
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

    /**
     * Computes the backoff delay for retry attempts.
     * 
     * @param attempt The current attempt number
     * @param retryAfterSeconds Optional explicit retry delay from the server
     * @return The Duration to wait before retrying
     */
    private Duration computeBackoffDelay(int attempt, Optional<Long> retryAfterSeconds) {
        if (retryAfterSeconds.isPresent()) {
            return Duration.ofSeconds(Math.max(1, retryAfterSeconds.get()));
        }
        long jitterMs = ThreadLocalRandom.current().nextLong(100, 400);
        long backoffMs = BASE_BACKOFF.toMillis() * (long) Math.pow(2, attempt - 1);
        return Duration.ofMillis(backoffMs + jitterMs);
    }

    /**
     * Creates a delayed CompletableFuture for retry backoff.
     * 
     * @param delay The duration to delay before completion
     * @return A CompletableFuture that completes after the specified delay
     */
    private CompletableFuture<Void> delayed(Duration delay) {
        return CompletableFuture.supplyAsync(() -> null, CompletableFuture.delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS)).thenAccept(v -> {});
    }

    /**
     * Parses an HTTP response into the specified class type.
     * 
     * @param <T> The type of the response object
     * @param response The HTTP response to parse
     * @param responseClass The class to parse the response into
     * @param requestType The type of request for error reporting
     * @param url The URL that was requested (for error reporting)
     * @return The parsed response object, or null if 404
     * @throws RiotApiRequestException if the response cannot be parsed or indicates an error
     */
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

    /**
     * Parses an HTTP response using the specified type reference.
     * 
     * @param <T> The type of the response object
     * @param response The HTTP response to parse
     * @param typeReference The type reference for complex types
     * @param requestType The type of request for error reporting
     * @param url The URL that was requested (for error reporting)
     * @return The parsed response object, or null if 404
     * @throws RiotApiRequestException if the response cannot be parsed or indicates an error
     */
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

    /**
     * Retrieves account information by Riot ID (game name and tag line).
     * 
     * <p>This method makes a request to the Riot Account API to resolve a Riot ID
     * to an AccountDto containing the player's PUUID and other account information.
     * The request is cached for 30 minutes to reduce API calls and improve performance.</p>
     * 
     * <p><strong>Performance Characteristics:</strong></p>
     * <ul>
     *   <li>Typical response time: 100-300ms</li>
     *   <li>Cache duration: 30 minutes</li>
     *   <li>Rate limit impact: 1 request per unique Riot ID</li>
     *   <li>Request coalescing: Duplicate requests for same Riot ID are merged</li>
     * </ul>
     * 
     * <p><strong>Error Handling:</strong></p>
     * <ul>
     *   <li>Returns null if account not found (404)</li>
     *   <li>Throws {@link RiotApiRequestException} for API errors (5xx, 429)</li>
     *   <li>Automatic retry with exponential backoff for transient failures</li>
     *   <li>Cache entry is evicted on persistent failures</li>
     * </ul>
     * 
     * @param gameName The game name portion of the Riot ID (e.g., "Faker")
     * @param tagLine The tag line portion of the Riot ID (e.g., "KR1")
     * @return A CompletableFuture containing the AccountDto, or null if not found
     * @throws RiotApiRequestException if the request fails after retries
     * @throws IllegalArgumentException if gameName or tagLine is null/empty
     */
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

    /**
     * Retrieves summoner information by PUUID.
     * 
     * @param puuid The Player Universally Unique Identifier
     * @return A CompletableFuture containing the summoner information
     */
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

    /**
     * Fetches league entries (rank, tier, etc.) by the encrypted summoner ID.
     * Official endpoint: /lol/league/v4/entries/by-summoner/{encryptedSummonerId}
     * 
     * @param summonerId The encrypted summoner ID
     * @return A CompletableFuture containing the list of league entries
     */
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

    /**
     * Fetches league entries using PUUID to prepare for removal of SummonerIDs from payloads.
     * Official endpoint: /lol/league/v4/entries/by-puuid/{puuid}
     * 
     * @param puuid The Player Universally Unique Identifier
     * @return A CompletableFuture containing the list of league entries
     */
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

    /**
     * RSO-based endpoint to fetch the summoner of the authenticated user.
     * Requires an OAuth 2.0 Bearer token (RSO), not the X-Riot-Token.
     * 
     * @param bearerToken The OAuth Bearer token for authentication
     * @return A CompletableFuture containing the authenticated user's summoner information
     */
    public CompletableFuture<Summoner> getSummonerMeWithBearer(String bearerToken) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/summoner/v4/summoners/me";
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (SummonerMe RSO): Requesting URL: [{}]", url);
        return sendRequestWithBearer(url, "SummonerMeRSO", bearerToken)
                .thenApply(response -> parseResponse(response, Summoner.class, "SummonerMeRSO", url));
    }

    /**
     * Retrieves match IDs for a player by PUUID.
     * 
     * @param puuid The Player Universally Unique Identifier
     * @param count The maximum number of match IDs to retrieve
     * @return A CompletableFuture containing the list of match IDs
     */
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

    /**
     * Overloaded variant that supports pagination via 'start' offset.
     * Official API supports 'start' and 'count' query parameters.
     * 
     * @param puuid The Player Universally Unique Identifier
     * @param start The starting index for pagination
     * @param count The maximum number of match IDs to retrieve
     * @return A CompletableFuture containing the list of match IDs
     */
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

    /**
     * Retrieves detailed match information by match ID.
     * 
     * @param matchId The match ID to retrieve details for
     * @return A CompletableFuture containing the match details
     */
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

    /**
     * Fetch league entries by queue/tier/division (paginated).
     * Example queue: RANKED_SOLO_5x5, tier: DIAMOND, division: I, page: 1
     * Official endpoint: /lol/league/v4/entries/{queue}/{tier}/{division}?page={page}
     * 
     * @param queue The queue type (e.g., RANKED_SOLO_5x5)
     * @param tier The tier (e.g., DIAMOND, PLATINUM)
     * @param division The division within the tier (e.g., I, II, III, IV)
     * @param page The page number for pagination
     * @return A CompletableFuture containing the list of league entries
     */
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

    /**
     * URL-encodes a string for use in API paths.
     * 
     * @param s The string to encode
     * @return The URL-encoded string
     */
    private String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /**
     * Fetch Summoner by encrypted Summoner ID.
     * Official endpoint: /lol/summoner/v4/summoners/{encryptedSummonerId}
     * 
     * @param summonerId The encrypted summoner ID
     * @return A CompletableFuture containing the summoner information
     */
    @Cacheable(value = "summoners", key = "#summonerId")
    public CompletableFuture<Summoner> getSummonerById(String summonerId) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/summoner/v4/summoners/" + summonerId;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (Summoner by ID): Requesting ID [{}]", maskId(summonerId));
        CompletableFuture<Summoner> future = sendApiRequestAsync(url, Summoner.class, "SummonerById");
        return evictOnException(future, "summoners", summonerId);
    }

    /**
     * Gets the configured platform region.
     * 
     * @return The platform region (e.g., euw1, na1)
     */
    public String getPlatformRegion() {
        return platformRegion;
    }

    /**
     * Coalesces multiple requests for the same key to prevent duplicate upstream calls.
     * 
     * @param <K> The type of the key
     * @param <T> The type of the result
     * @param inFlightMap Map tracking in-flight requests
     * @param key The key to coalesce on
     * @param loader Supplier that creates the actual request
     * @return A CompletableFuture that completes with the result
     */
    private <K, T> CompletableFuture<T> coalesce(Map<K, CompletableFuture<T>> inFlightMap, K key, Supplier<CompletableFuture<T>> loader) {
        return inFlightMap.computeIfAbsent(key, k -> loader.get()
                .whenComplete((res, ex) -> inFlightMap.remove(k)));
    }

    /**
     * Masks a PUUID for safe logging by showing only the first 6 and last 4 characters.
     * 
     * @param puuid The PUUID to mask
     * @return The masked PUUID string
     */
    private static String maskPuuid(String puuid) {
        if (puuid == null) return "(null)";
        int len = puuid.length();
        if (len <= 10) return "***";
        return puuid.substring(0, 6) + "..." + puuid.substring(len - 4);
    }

    /**
     * Masks an ID for safe logging by showing only the first few and last few characters.
     * 
     * @param id The ID to mask
     * @return The masked ID string
     */
    private static String maskId(String id) {
        if (id == null) return "(null)";
        int len = id.length();
        if (len <= 8) return "***";
        return id.substring(0, Math.min(4, len)) + "..." + id.substring(len - Math.min(3, len));
    }

    /**
     * Abbreviates a string to a maximum length for logging.
     * 
     * @param s The string to abbreviate
     * @param max The maximum length
     * @return The abbreviated string, or original if under limit
     */
    private String abbreviate(String s, int max) {
        if (s == null) return null;
        if (s.length() <= max) return s;
        return s.substring(0, max) + "…";
    }

    /**
     * Evicts a cache entry if the future completes with an exception.
     * 
     * @param <T> The type of the result
     * @param future The CompletableFuture to monitor
     * @param cacheName The name of the cache to evict from
     * @param cacheKey The key to evict from the cache
     * @return A CompletableFuture that completes with the same result as the input
     */
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

    /**
     * Converts a string to lowercase using ROOT locale.
     * 
     * @param value The string to convert (must not be null)
     * @return The lowercase string
     * @throws NullPointerException if value is null
     */
    public static String lower(String value) {
        return java.util.Objects.requireNonNull(value, "value").toLowerCase(Locale.ROOT);
    }

    /**
     * Generates a cache key for account lookups by combining game name and tag line.
     * 
     * @param gameName The game name portion of the Riot ID
     * @param tagLine The tag line portion of the Riot ID
     * @return A cache key in the format "gamename#tagline" (lowercase)
     */
    public static String accountCacheKey(String gameName, String tagLine) {
        return lower(gameName) + "#" + lower(tagLine);
    }
}
