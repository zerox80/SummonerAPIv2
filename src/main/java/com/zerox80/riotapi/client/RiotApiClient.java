// Package declaration: Defines that this class belongs to the client package
package com.zerox80.riotapi.client;

// Import for Jackson JSON parsing exceptions
import com.fasterxml.jackson.core.JsonProcessingException;
// Import for Jackson to deserialize generic types (e.g., List<LeagueEntryDTO>)
import com.fasterxml.jackson.core.type.TypeReference;
// Import for configuring deserialization behavior
import com.fasterxml.jackson.databind.DeserializationFeature;
// Import of the main class for JSON serialization/deserialization
import com.fasterxml.jackson.databind.ObjectMapper;
// Import for configuration of property naming (camelCase, snake_case, etc.)
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
// Import of our model class for account data from Riot
import com.zerox80.riotapi.model.AccountDto;
// Import for ranked league entries (rank, division, LP)
import com.zerox80.riotapi.model.LeagueEntryDTO;
// Import for detailed match data from the V5 API
import com.zerox80.riotapi.model.MatchV5Dto;
// Import for summoner basic data (level, name, icon)
import com.zerox80.riotapi.model.Summoner;
// Import for the logging interface from SLF4J
import org.slf4j.Logger;
// Import for the factory to create logger instances
import org.slf4j.LoggerFactory;
// Import for automatic dependency injection by Spring
import org.springframework.beans.factory.annotation.Autowired;
// Import to inject values from application.properties
import org.springframework.beans.factory.annotation.Value;
// Import for direct cache access for manual evicting
import org.springframework.cache.Cache;
// Import of the annotation to cache method results
import org.springframework.cache.annotation.Cacheable;
// Import for access to all configured caches
import org.springframework.cache.CacheManager;
// Import to register this class as a Spring bean
import org.springframework.stereotype.Component;
// Import for metrics registry for performance monitoring
import io.micrometer.core.instrument.MeterRegistry;
// Import for timer metrics to measure latency
import io.micrometer.core.instrument.Timer;

// Import for general IO exceptions
import java.io.IOException;
// Import for URI construction (URLs for API calls)
import java.net.URI;
// Import for URL encoding of parameters (prevents injection)
import java.net.URLEncoder;
// Import of the modern Java HTTP client (since Java 11)
import java.net.http.HttpClient;
// Import for HTTP request builder
import java.net.http.HttpRequest;
// Import for HTTP response objects
import java.net.http.HttpResponse;
// Import for UTF-8 encoding standard
import java.nio.charset.StandardCharsets;
// Import for time durations (timeouts, delays)
import java.time.Duration;
// Import for time points with timezone (for Retry-After header)
import java.time.ZonedDateTime;
// Import for formatting date strings
import java.time.format.DateTimeFormatter;
// Import for list data structure
import java.util.List;
// Import for locale (language settings, here for toLowerCase)
import java.util.Locale;
// Import for optional pattern (avoids null checks)
import java.util.Optional;
// Import for map interface
import java.util.Map;
// Import for thread-safe hash map (important for concurrency)
import java.util.concurrent.ConcurrentHashMap;
// Import for time units in concurrent operations
import java.util.concurrent.TimeUnit;
// Import for thread-safe random generator (for jitter in retry logic)
import java.util.concurrent.ThreadLocalRandom;
// Import for asynchronous operations with callbacks
import java.util.concurrent.CompletableFuture;
// Import for supplier functional interface
import java.util.function.Supplier;
// Import for semaphore to limit concurrent requests
import java.util.concurrent.Semaphore;
// Import for thread-safe integer counter
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main HTTP client for all communication with the Riot Games API.
 *
 * This client handles:
 * - Riot API authentication and rate limiting
 * - Automatic retry logic with exponential backoff
 * - Request coalescing to prevent duplicate API calls
 * - Comprehensive metrics and logging
 * - Spring Cache integration
 *
 * All methods return CompletableFuture for asynchronous, non-blocking
 * operations.
 */
@Component
public class RiotApiClient {

    // Semaphore limits the number of concurrent outgoing HTTP requests
    // Prevents overloading the Riot API and reaching rate limits
    // final: Value cannot be changed after initialization
    // Semaphore limits the number of concurrent outgoing HTTP requests
    // Prevents overloading the Riot API and reaching rate limits
    // final: Value cannot be changed after initialization
    private final Semaphore outboundLimiter;
    private final java.util.Queue<CompletableFuture<Void>> waitingRequests = new java.util.concurrent.ConcurrentLinkedQueue<>();

    // static final: Class-wide constant, logger for this specific class
    // LoggerFactory.getLogger(): Creates a logger with the class name as category
    private static final Logger logger = LoggerFactory.getLogger(RiotApiClient.class);

    // final: Immutable API key for authentication with Riot
    private final String apiKey;

    // final: Platform region (e.g., "euw1" for Europe West)
    private final String platformRegion;

    // final: Regional routing for match history APIs (e.g., "europe")
    private final String regionalRoute;

    // final: Reusable HTTP client for all requests
    private final HttpClient httpClient;

    // final: JSON mapper for serialization/deserialization
    private final ObjectMapper objectMapper;

    // final: Base URL for Community Dragon CDN (profile icons, etc.)
    private final String communityDragonUrl;

    // final: Registry for metrics (Prometheus, Grafana)
    private final MeterRegistry meterRegistry;

    // final: Maximum number of concurrent outgoing requests
    private final int maxConcurrentOutbound;

    // final: User-Agent string for HTTP headers (identifies our application)
    private final String userAgent;

    // final: Manager for all cache instances in the application
    private final CacheManager cacheManager;

    // In-flight request coalescing maps - prevents duplicate API calls
    // when multiple threads request the same data simultaneously (cache miss)
    // ConcurrentHashMap: Thread-safe map for parallel access
    // Key: Account identifier, Value: Future with the result
    private final Map<String, CompletableFuture<AccountDto>> accountByRiotIdInFlight = new ConcurrentHashMap<>();

    // Map for in-flight summoner requests by PUUID
    private final Map<String, CompletableFuture<Summoner>> summonerByPuuidInFlight = new ConcurrentHashMap<>();

    // Map for in-flight league requests by summoner ID
    private final Map<String, CompletableFuture<List<LeagueEntryDTO>>> leagueBySummonerIdInFlight = new ConcurrentHashMap<>();

    // Map for in-flight league requests by PUUID
    private final Map<String, CompletableFuture<List<LeagueEntryDTO>>> leagueByPuuidInFlight = new ConcurrentHashMap<>();

    // Map for in-flight match ID requests
    private final Map<String, CompletableFuture<List<String>>> matchIdsInFlight = new ConcurrentHashMap<>();

    // Map for in-flight match detail requests
    private final Map<String, CompletableFuture<MatchV5Dto>> matchDetailsInFlight = new ConcurrentHashMap<>();

    // static final: Type token for Jackson to deserialize List<LeagueEntryDTO>
    // TypeReference: Preserves generic type information at runtime (bypasses type
    // erasure)
    // {} at the end: Anonymous inner class extending TypeReference
    private static final TypeReference<List<LeagueEntryDTO>> LEAGUE_LIST_TYPE = new TypeReference<>() {
    };

    // Type token for deserialization of string lists (match IDs)
    private static final TypeReference<List<String>> MATCH_ID_LIST_TYPE = new TypeReference<>() {
    };

    /**
     * Constructs the RiotApiClient with all required dependencies.
     *
     * @param apiKey                Riot API key from configuration
     * @param platformRegion        Platform region (e.g., euw1, na1, kr)
     * @param communityDragonUrl    Base URL for Community Dragon CDN
     * @param userAgent             User-Agent header for API requests
     * @param objectMapper          Jackson ObjectMapper for JSON processing
     * @param meterRegistry         Metrics registry for monitoring
     * @param httpClient            HTTP client for making requests
     * @param maxConcurrentOutbound Maximum number of concurrent outbound requests
     * @param cacheManager          Spring cache manager for cache operations
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
        // Copy and harden the mapper: Riot APIs use lowerCamelCase; ignore unknown
        // fields
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
     * Constructs the full URL for a summoner profile icon.
     *
     * @param iconId The profile icon ID
     * @return Full URL to the profile icon image
     */
    public String getProfileIconUrl(int iconId) {
        return communityDragonUrl + "/" + iconId + ".jpg";
    }

    /**
     * Determines the regional routing endpoint based on the platform region.
     * Regional routes are used for match history and account APIs.
     *
     * @param platform The platform region (e.g., euw1, na1, kr)
     * @return The regional routing endpoint (europe, americas, asia, sea)
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
                logger.warn(
                        "Warning: Unknown platform region '{}' for determining regional route. Defaulting to platform itself.",
                        platform);
                return platform;
        }
    }

    /**
     * Sends an async API request and parses the response into a single object.
     *
     * @param url           The full API endpoint URL
     * @param responseClass The class to deserialize the response into
     * @param requestType   Description of the request type for logging/metrics
     * @param <T>           The type of the response object
     * @return CompletableFuture containing the parsed response
     */
    private <T> CompletableFuture<T> sendApiRequestAsync(String url, Class<T> responseClass, String requestType) {
        return sendRequest(url, requestType)
                .thenApply(response -> parseResponse(response, responseClass, requestType, url));
    }

    /**
     * Sends an async API request and parses the response into a generic type.
     *
     * @param url           The full API endpoint URL
     * @param typeReference TypeReference for generic types (e.g., List<T>)
     * @param requestType   Description of the request type for logging/metrics
     * @param <T>           The type of the response object
     * @return CompletableFuture containing the parsed response
     */
    private <T> CompletableFuture<T> sendApiRequestAsync(String url, TypeReference<T> typeReference,
            String requestType) {
        return sendRequest(url, requestType)
                .thenApply(response -> parseResponse(response, typeReference, requestType, url));
    }

    // Maximum retry attempts for failed requests
    private static final int MAX_ATTEMPTS = 3;

    // Base backoff duration for exponential retry
    private static final Duration BASE_BACKOFF = Duration.ofSeconds(2);

    /**
     * Sends an HTTP request with automatic retry logic and instrumentation.
     *
     * @param url         The full API endpoint URL
     * @param requestType Description of the request type for logging/metrics
     * @return CompletableFuture containing the HTTP response
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
                        if (status == 429)
                            statusTag = "429";
                        else if (status >= 200 && status < 300)
                            statusTag = "2xx";
                        else if (status >= 400 && status < 500)
                            statusTag = "4xx";
                        else if (status >= 500)
                            statusTag = "5xx";
                        else
                            statusTag = String.valueOf(status);
                    }
                    meterRegistry.counter("riotapi.client.requests", "type", requestType, "status", statusTag)
                            .increment();
                    Timer timer = meterRegistry.timer("riotapi.client.latency", "type", requestType, "status",
                            statusTag, "retries", String.valueOf(retries.get()));
                    sample.stop(timer);
                });
    }

    /**
     * Sends an HTTP request with Bearer token authentication (for RSO endpoints).
     *
     * @param url         The full API endpoint URL
     * @param requestType Description of the request type for logging/metrics
     * @param bearerToken RSO Bearer token for authentication
     * @return CompletableFuture containing the HTTP response
     */
    private CompletableFuture<HttpResponse<String>> sendRequestWithBearer(String url, String requestType,
            String bearerToken) {
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
                        if (status == 429)
                            statusTag = "429";
                        else if (status >= 200 && status < 300)
                            statusTag = "2xx";
                        else if (status >= 400 && status < 500)
                            statusTag = "4xx";
                        else if (status >= 500)
                            statusTag = "5xx";
                        else
                            statusTag = String.valueOf(status);
                    }
                    meterRegistry.counter("riotapi.client.requests", "type", requestType, "status", statusTag)
                            .increment();
                    Timer timer = meterRegistry.timer("riotapi.client.latency", "type", requestType, "status",
                            statusTag, "retries", String.valueOf(retries.get()));
                    sample.stop(timer);
                });
    }

    /**
     * Executes the HTTP request with automatic retry for transient failures.
     * Implements exponential backoff with jitter and respects Retry-After headers.
     *
     * @param request     The HTTP request to send
     * @param requestType Description of the request type for logging/metrics
     * @param url         The full API endpoint URL
     * @param attempt     Current attempt number (1-based)
     * @param retries     Atomic counter tracking total retry attempts
     * @return CompletableFuture containing the HTTP response
     */
    private CompletableFuture<HttpResponse<String>> sendWithRetryInstrumented(HttpRequest request, String requestType,
            String url, int attempt, AtomicInteger retries) {
        return acquirePermitAsync()
                .thenCompose(v -> httpClient
                        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .handle((response, throwable) -> new Object[] { response, throwable })
                        .whenComplete((pair, t) -> releasePermit()))
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
                            return delayed(delay).thenCompose(
                                    v -> sendWithRetryInstrumented(request, requestType, url, attempt + 1, retries));
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
                        return delayed(delay).thenCompose(
                                v -> sendWithRetryInstrumented(request, requestType, url, attempt + 1, retries));
                    }

                    return CompletableFuture.completedFuture(response);
                });
    }

    /**
     * Asynchronously acquires a permit from the semaphore for rate limiting.
     * Uses non-blocking polling with delayed retry.
     *
     * @return CompletableFuture that completes when a permit is acquired
     */
    private CompletableFuture<Void> acquirePermitAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        retryAcquire(future);
        return future;
    }

    /**
     * Recursively tries to acquire a semaphore permit with polling.
     * Retries every 25ms if permit is not available.
     *
     * @param future The future to complete when permit is acquired
     */
    private void retryAcquire(CompletableFuture<Void> future) {
        if (outboundLimiter.tryAcquire()) {
            future.complete(null);
        } else {
            waitingRequests.add(future);
            // Double check in case a permit was released while we were adding
            if (outboundLimiter.tryAcquire()) {
                CompletableFuture<Void> f = waitingRequests.poll();
                if (f != null) {
                    f.complete(null);
                } else {
                    outboundLimiter.release();
                }
            }
        }
    }

    private void releasePermit() {
        outboundLimiter.release();
        CompletableFuture<Void> next = waitingRequests.poll();
        if (next != null) {
            if (outboundLimiter.tryAcquire()) {
                next.complete(null);
            } else {
                // Should not happen if we just released, but race conditions exist
                waitingRequests.add(next);
            }
        }
    }

    /**
     * Parses the Retry-After header from the HTTP response.
     * Supports both delta-seconds and HTTP-date formats per RFC 7231.
     *
     * @param response The HTTP response containing the header
     * @return Optional containing retry delay in seconds, empty if header missing
     *         or invalid
     */
    private Optional<Long> parseRetryAfterSeconds(HttpResponse<String> response) {
        return response.headers().firstValue("Retry-After").flatMap(value -> {
            if (value == null)
                return Optional.empty();
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
     * Respects Retry-After header if present, otherwise uses exponential backoff
     * with jitter.
     *
     * @param attempt           Current attempt number (1-based)
     * @param retryAfterSeconds Optional retry delay from Retry-After header
     * @return Duration to wait before retrying
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
     * Creates a CompletableFuture that completes after the specified delay.
     *
     * @param delay Duration to wait
     * @return CompletableFuture that completes after the delay
     */
    private CompletableFuture<Void> delayed(Duration delay) {
        return CompletableFuture
                .supplyAsync(() -> null, CompletableFuture.delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS))
                .thenAccept(v -> {
                });
    }

    /**
     * Parses HTTP response body into a single object using class type.
     * Handles 200 OK, 404 Not Found, and error responses.
     *
     * @param response      The HTTP response to parse
     * @param responseClass The class to deserialize into
     * @param requestType   Description of the request type for logging
     * @param url           The original request URL for logging
     * @param <T>           The type of the response object
     * @return Parsed object, or null if 404, throws exception on error
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
            logger.error("API Request Failed ({}): status={} url={} bodySnippet={}", requestType, response.statusCode(),
                    url, snippet);
            throw new RiotApiRequestException(
                    "API request (" + requestType + ") failed with status code: " + response.statusCode());
        }
    }

    /**
     * Parses HTTP response body into a generic type using TypeReference.
     * Handles 200 OK, 404 Not Found, and error responses.
     *
     * @param response      The HTTP response to parse
     * @param typeReference TypeReference for generic types (e.g., List<T>)
     * @param requestType   Description of the request type for logging
     * @param url           The original request URL for logging
     * @param <T>           The type of the response object
     * @return Parsed object, or null if 404, throws exception on error
     */
    private <T> T parseResponse(HttpResponse<String> response, TypeReference<T> typeReference, String requestType,
            String url) {
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
            logger.error("API Request Failed ({}): status={} url={} bodySnippet={}", requestType, response.statusCode(),
                    url, snippet);
            throw new RiotApiRequestException(
                    "API request (" + requestType + ") failed with status code: " + response.statusCode());
        }
    }

    /**
     * Retrieves account information by Riot ID (game name + tag line).
     * Results are cached to reduce API calls.
     *
     * @param gameName The summoner's game name
     * @param tagLine  The summoner's tag line (e.g., "EUW", "NA1")
     * @return CompletableFuture containing the AccountDto, or null if not found
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
     * Results are cached to reduce API calls.
     *
     * @param puuid The player's PUUID
     * @return CompletableFuture containing the Summoner, or null if not found
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
     * Retrieves ranked league entries by summoner ID.
     * Results are cached to reduce API calls.
     *
     * @param summonerId The summoner's encrypted ID
     * @return CompletableFuture containing list of LeagueEntryDTOs (may be empty)
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
     * Retrieves ranked league entries by PUUID.
     * Results are cached to reduce API calls.
     *
     * @param puuid The player's PUUID
     * @return CompletableFuture containing list of LeagueEntryDTOs (may be empty)
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
     * Retrieves authenticated summoner data using RSO Bearer token.
     * This endpoint uses the /me endpoint with OAuth authentication.
     *
     * @param bearerToken RSO Bearer token from OAuth flow
     * @return CompletableFuture containing the authenticated Summoner
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
     * Results are cached to reduce API calls.
     *
     * @param puuid The player's PUUID
     * @param count Maximum number of match IDs to return
     * @return CompletableFuture containing list of match IDs (may be empty)
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
     * Retrieves match IDs for a player by PUUID with pagination.
     * Results are cached to reduce API calls.
     *
     * @param puuid The player's PUUID
     * @param start Starting index for pagination
     * @param count Maximum number of match IDs to return
     * @return CompletableFuture containing list of match IDs (may be empty)
     */
    @Cacheable(value = "matchIds", key = "#puuid + '-' + #start + '-' + #count")
    public CompletableFuture<List<String>> getMatchIdsByPuuid(String puuid, int start, int count) {
        String host = this.regionalRoute + ".api.riotgames.com";
        String path = "/lol/match/v5/matches/by-puuid/" + puuid + "/ids?start=" + start + "&count=" + count;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (MatchIdsPaged): PUUID [{}], start {}, count {}", maskPuuid(puuid), start,
                count);
        String key = puuid + "-" + start + "-" + count;
        CompletableFuture<List<String>> future = coalesce(matchIdsInFlight, key,
                () -> sendApiRequestAsync(url, MATCH_ID_LIST_TYPE, "MatchIdsPaged")
                        .thenApply(list -> list != null ? list : List.of()));
        return evictOnException(future, "matchIds", key);
    }

    /**
     * Retrieves detailed match information by match ID.
     * Results are cached to reduce API calls.
     *
     * @param matchId The match ID (e.g., "EUW1_6234567890")
     * @return CompletableFuture containing MatchV5Dto, or null if not found
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
     * Retrieves league entries by queue type, tier, and division with pagination.
     * Used for fetching ladder data and LP tracking.
     * Results are cached to reduce API calls.
     *
     * @param queue    Queue type (e.g., "RANKED_SOLO_5x5")
     * @param tier     Tier (e.g., "DIAMOND", "MASTER")
     * @param division Division (e.g., "I", "II", "III", "IV")
     * @param page     Page number (1-based)
     * @return CompletableFuture containing list of LeagueEntryDTOs
     */
    @Cacheable(value = "leagueEntries", key = "#queue + '|' + #tier + '|' + #division + '|' + #page")
    public CompletableFuture<List<LeagueEntryDTO>> getEntriesByQueueTierDivision(String queue, String tier,
            String division, int page) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/league/v4/entries/" + urlEncode(queue) + "/" + urlEncode(tier) + "/" + urlEncode(division)
                + "?page=" + page;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (Entries {} {} {} p{}): {}", queue, tier, division, page, url);
        String cacheKey = queue + "|" + tier + "|" + division + "|" + page;
        CompletableFuture<List<LeagueEntryDTO>> future = sendApiRequestAsync(url, LEAGUE_LIST_TYPE,
                "LeagueEntriesByTier");
        return evictOnException(future, "leagueEntries", cacheKey);
    }

    /**
     * URL-encodes a string using UTF-8 encoding.
     *
     * @param s The string to encode
     * @return URL-encoded string
     */
    private String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /**
     * Retrieves summoner information by summoner ID.
     * Results are cached to reduce API calls.
     *
     * @param summonerId The summoner's encrypted ID
     * @return CompletableFuture containing Summoner, or null if not found
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
     * @return The platform region (e.g., "euw1", "na1")
     */
    public String getPlatformRegion() {
        return platformRegion;
    }

    /**
     * Coalesces multiple concurrent requests for the same resource.
     * If a request is already in flight for the given key, returns the existing
     * future.
     * Otherwise, creates a new request and registers it.
     *
     * @param inFlightMap Map tracking in-flight requests
     * @param key         Unique identifier for the request
     * @param loader      Supplier that creates the actual request
     * @param <K>         Type of the key
     * @param <T>         Type of the result
     * @return CompletableFuture for the result (shared if in flight)
     */
    private <K, T> CompletableFuture<T> coalesce(Map<K, CompletableFuture<T>> inFlightMap, K key,
            Supplier<CompletableFuture<T>> loader) {
        return inFlightMap.computeIfAbsent(key, k -> loader.get()
                .whenComplete((res, ex) -> inFlightMap.remove(k)));
    }

    /**
     * Masks PUUID for logging to protect sensitive data.
     * Shows first 6 and last 4 characters.
     *
     * @param puuid The PUUID to mask
     * @return Masked PUUID string
     */
    private static String maskPuuid(String puuid) {
        if (puuid == null)
            return "(null)";
        int len = puuid.length();
        if (len <= 10)
            return "***";
        return puuid.substring(0, 6) + "..." + puuid.substring(len - 4);
    }

    /**
     * Masks ID for logging to protect sensitive data.
     * Shows first 4 and last 3 characters.
     *
     * @param id The ID to mask
     * @return Masked ID string
     */
    private static String maskId(String id) {
        if (id == null)
            return "(null)";
        int len = id.length();
        if (len <= 8)
            return "***";
        return id.substring(0, Math.min(4, len)) + "..." + id.substring(len - Math.min(3, len));
    }

    /**
     * Abbreviates a string to maximum length for logging.
     * Appends ellipsis if truncated.
     *
     * @param s   The string to abbreviate
     * @param max Maximum length
     * @return Abbreviated string
     */
    private String abbreviate(String s, int max) {
        if (s == null)
            return null;
        if (s.length() <= max)
            return s;
        return s.substring(0, max) + "…";
    }

    /**
     * Evicts cache entry if the future completes with an exception.
     * Ensures failed requests don't poison the cache.
     *
     * @param future    The CompletableFuture to monitor
     * @param cacheName Name of the cache to evict from
     * @param cacheKey  Key to evict
     * @param <T>       Type of the future result
     * @return The same future, for chaining
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
     * Used for case-insensitive cache keys.
     *
     * @param value The string to convert
     * @return Lowercase string
     */
    public static String lower(String value) {
        return java.util.Objects.requireNonNull(value, "value").toLowerCase(Locale.ROOT);
    }

    /**
     * Creates a normalized cache key for account lookup.
     * Case-insensitive by converting both parts to lowercase.
     *
     * @param gameName The summoner's game name
     * @param tagLine  The summoner's tag line
     * @return Normalized cache key
     */
    public static String accountCacheKey(String gameName, String tagLine) {
        return lower(gameName) + "#" + lower(tagLine);
    }
}
