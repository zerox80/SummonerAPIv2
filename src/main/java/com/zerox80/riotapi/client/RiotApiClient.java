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
import org.springframework.cache.annotation.Cacheable;
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
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.CompletableFuture;
 
import java.util.function.Supplier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

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

    // In-flight request coalescing maps to prevent duplicate upstream calls on cache misses
    private final Map<String, CompletableFuture<AccountDto>> accountByRiotIdInFlight = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<Summoner>> summonerByPuuidInFlight = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<List<LeagueEntryDTO>>> leagueBySummonerIdInFlight = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<List<LeagueEntryDTO>>> leagueByPuuidInFlight = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<List<String>>> matchIdsInFlight = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<MatchV5Dto>> matchDetailsInFlight = new ConcurrentHashMap<>();

    private static final TypeReference<List<LeagueEntryDTO>> LEAGUE_LIST_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<String>> MATCH_ID_LIST_TYPE = new TypeReference<>() {};

    @Autowired
    public RiotApiClient(@Value("${riot.api.key:}") String apiKey,
                         @Value("${riot.api.region:euw1}") String platformRegion,
                         @Value("${riot.api.community-dragon.url:https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/profile-icons}") String communityDragonUrl,
                         ObjectMapper objectMapper,
                         MeterRegistry meterRegistry,
                         HttpClient httpClient,
                         @Value("${riot.api.max-concurrent:15}") int maxConcurrentOutbound) {
        this.apiKey = apiKey;
        this.platformRegion = platformRegion.toLowerCase();
        this.regionalRoute = determineRegionalRoute(this.platformRegion);
        this.communityDragonUrl = communityDragonUrl;
        this.meterRegistry = meterRegistry;
        this.maxConcurrentOutbound = maxConcurrentOutbound > 0 ? maxConcurrentOutbound : 15;
        // Copy and harden the mapper: Riot APIs use lowerCamelCase; ignore unknown fields
        this.objectMapper = objectMapper.copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        this.httpClient = httpClient;
        this.outboundLimiter = new Semaphore(this.maxConcurrentOutbound);

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
                .header("User-Agent", "SummonerAPI/2.0 (github.com/zerox80/SummonerAPI)")
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
                .header("User-Agent", "SummonerAPI/2.0 (github.com/zerox80/SummonerAPI)")
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
                            logger.warn("Request {} to {} failed (attempt {}/{}). Retrying in {} ms. Cause: {}",
                                    requestType, url, attempt, MAX_ATTEMPTS, delay.toMillis(), throwable.toString());
                            retries.incrementAndGet();
                            meterRegistry.counter("riotapi.client.retries", "type", requestType).increment();
                            return delayed(delay).thenCompose(v -> sendWithRetryInstrumented(request, requestType, url, attempt + 1, retries));
                        }
                        CompletableFuture<HttpResponse<String>> failed = new CompletableFuture<>();
                        failed.completeExceptionally(throwable);
                        return failed;
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
            try {
                return Optional.of(Long.parseLong(value.trim()));
            } catch (NumberFormatException e) {
                return Optional.empty();
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

    @Cacheable(value = "accounts", key = "#gameName.toLowerCase() + '#' + #tagLine.toLowerCase()")
    public CompletableFuture<AccountDto> getAccountByRiotId(String gameName, String tagLine) {
        String encodedGameName = URLEncoder.encode(gameName, StandardCharsets.UTF_8).replace("+", "%20");
        String encodedTagLine = URLEncoder.encode(tagLine, StandardCharsets.UTF_8);

        String host = this.regionalRoute + ".api.riotgames.com";
        String path = "/riot/account/v1/accounts/by-riot-id/" + encodedGameName + "/" + encodedTagLine;
        String url = "https://" + host + path;

        logger.debug(">>> RiotApiClient (Account): Requesting RAW Riot ID: [{}#{}]", gameName, tagLine);
        logger.debug(">>> RiotApiClient (Account): Requesting ENCODED URL: [{}]", url);

        String inflightKey = gameName.toLowerCase() + "#" + tagLine.toLowerCase();
        return coalesce(accountByRiotIdInFlight, inflightKey,
                () -> sendApiRequestAsync(url, AccountDto.class, "Account"));
    }

    @Cacheable(value = "summoners", key = "#puuid")
    public CompletableFuture<Summoner> getSummonerByPuuid(String puuid) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/summoner/v4/summoners/by-puuid/" + puuid;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (Summoner): Requesting by PUUID [{}]", maskPuuid(puuid));
        return coalesce(summonerByPuuidInFlight, puuid,
                () -> sendApiRequestAsync(url, Summoner.class, "Summoner"));
    }

    /**
     * Fetches league entries (rank, tier, etc.) by the encrypted summoner ID.
     * Official endpoint: /lol/league/v4/entries/by-summoner/{encryptedSummonerId}
     */
    @Cacheable(value = "leagueEntries", key = "#summonerId")
    public CompletableFuture<List<LeagueEntryDTO>> getLeagueEntriesBySummonerId(String summonerId) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/league/v4/entries/by-summoner/" + summonerId;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (LeagueEntries): Requesting URL: [{}]", url);
        return coalesce(leagueBySummonerIdInFlight, summonerId,
                () -> sendApiRequestAsync(url, LEAGUE_LIST_TYPE, "LeagueEntries")
                        .thenApply(list -> list != null ? list : List.of()));
    }

    /**
     * Fetches league entries using PUUID to prepare for removal of SummonerIDs from payloads.
     * Official endpoint: /lol/league/v4/entries/by-puuid/{puuid}
     */
    @Cacheable(value = "leagueEntries", key = "#puuid")
    public CompletableFuture<List<LeagueEntryDTO>> getLeagueEntriesByPuuid(String puuid) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/league/v4/entries/by-puuid/" + puuid;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (LeagueEntries PUUID): Requesting by PUUID [{}]", maskPuuid(puuid));
        return coalesce(leagueByPuuidInFlight, puuid,
                () -> sendApiRequestAsync(url, LEAGUE_LIST_TYPE, "LeagueEntriesByPuuid")
                        .thenApply(list -> list != null ? list : List.of()));
    }

    /**
     * RSO-based endpoint to fetch the summoner of the authenticated user.
     * Requires an OAuth 2.0 Bearer token (RSO), not the X-Riot-Token.
     */
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
        return coalesce(matchIdsInFlight, key,
                () -> sendApiRequestAsync(url, MATCH_ID_LIST_TYPE, "MatchIds")
                        .thenApply(list -> list != null ? list : List.of()));
    }

    /**
     * Overloaded variant that supports pagination via 'start' offset.
     * Official API supports 'start' and 'count' query parameters.
     */
    @Cacheable(value = "matchIds", key = "#puuid + '-' + #start + '-' + #count")
    public CompletableFuture<List<String>> getMatchIdsByPuuid(String puuid, int start, int count) {
        String host = this.regionalRoute + ".api.riotgames.com";
        String path = "/lol/match/v5/matches/by-puuid/" + puuid + "/ids?start=" + start + "&count=" + count;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (MatchIdsPaged): PUUID [{}], start {}, count {}", maskPuuid(puuid), start, count);
        String key = puuid + "-" + start + "-" + count;
        return coalesce(matchIdsInFlight, key,
                () -> sendApiRequestAsync(url, MATCH_ID_LIST_TYPE, "MatchIdsPaged")
                        .thenApply(list -> list != null ? list : List.of()));
    }

    @Cacheable(value = "matchDetails", key = "#matchId")
    public CompletableFuture<MatchV5Dto> getMatchDetails(String matchId) {
        String host = this.regionalRoute + ".api.riotgames.com";
        String path = "/lol/match/v5/matches/" + matchId;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (MatchDetails): Requesting URL: [{}]", url);
        return coalesce(matchDetailsInFlight, matchId,
                () -> sendApiRequestAsync(url, MatchV5Dto.class, "MatchDetails"));
    }

    /**
     * Fetch league entries by queue/tier/division (paginated).
     * Example queue: RANKED_SOLO_5x5, tier: DIAMOND, division: I, page: 1
     * Official endpoint: /lol/league/v4/entries/{queue}/{tier}/{division}?page={page}
     */
    @Cacheable(value = "leagueEntries", key = "#queue + '|' + #tier + '|' + #division + '|' + #page")
    public CompletableFuture<List<LeagueEntryDTO>> getEntriesByQueueTierDivision(String queue, String tier, String division, int page) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/league/v4/entries/" + urlEncode(queue) + "/" + urlEncode(tier) + "/" + urlEncode(division) + "?page=" + page;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (Entries {} {} {} p{}): {}", queue, tier, division, page, url);
        return sendApiRequestAsync(url, LEAGUE_LIST_TYPE, "LeagueEntriesByTier");
    }

    private String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /**
     * Fetch Summoner by encrypted Summoner ID.
     * Official endpoint: /lol/summoner/v4/summoners/{encryptedSummonerId}
     */
    @Cacheable(value = "summoners", key = "#summonerId")
    public CompletableFuture<Summoner> getSummonerById(String summonerId) {
        String host = this.platformRegion + ".api.riotgames.com";
        String path = "/lol/summoner/v4/summoners/" + summonerId;
        String url = "https://" + host + path;
        logger.debug(">>> RiotApiClient (Summoner by ID): Requesting ID [{}]", maskPuuid(summonerId));
        return sendApiRequestAsync(url, Summoner.class, "SummonerById");
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

    private String abbreviate(String s, int max) {
        if (s == null) return null;
        if (s.length() <= max) return s;
        return s.substring(0, max) + "…";
    }
}
