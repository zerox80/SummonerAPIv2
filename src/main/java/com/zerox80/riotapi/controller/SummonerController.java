// Package declaration - defines the namespace for the controller class
package com.zerox80.riotapi.controller;

// Import for Summoner model (represents player data)
import com.zerox80.riotapi.model.Summoner;
// Import for LeagueEntry DTO (Data Transfer Object for ranked entries)
import com.zerox80.riotapi.model.LeagueEntryDTO;
// Import for Match DTO Version 5 (match data from Riot API)
import com.zerox80.riotapi.model.MatchV5Dto;
// Import for Summoner Suggestion DTO (used for search suggestions)
import com.zerox80.riotapi.model.SummonerSuggestionDTO;
// Import for Riot API Service (communicates with Riot Games API)
import com.zerox80.riotapi.service.RiotApiService;
// Import for Data Dragon Service (loads champion and item data)
import com.zerox80.riotapi.service.DataDragonService;
// Import for Summoner profile data (aggregates various game data)
import com.zerox80.riotapi.model.SummonerProfileData;
// Import for SLF4J Logger (logging framework for structured logs)
import org.slf4j.Logger;
// Import for LoggerFactory (creates Logger instances)
import org.slf4j.LoggerFactory;
// Import for dependency injection via Autowired
import org.springframework.beans.factory.annotation.Autowired;
// Import for Value annotation (injects configuration values from application.properties)
import org.springframework.beans.factory.annotation.Value;
// Import for GetMapping annotation (defines HTTP GET endpoints)
import org.springframework.web.bind.annotation.GetMapping;
// Import for RequestParam annotation (binds URL parameters to method parameters)
import org.springframework.web.bind.annotation.RequestParam;
// Import for StringUtils (helps with string validation and manipulation)
import org.springframework.util.StringUtils;
// Import for ResponseEntity (enables HTTP status code and header control)
import org.springframework.http.ResponseEntity;
// Import for HttpStatus enum (defines HTTP status codes like 200, 404, 500)
import org.springframework.http.HttpStatus;
// Import for RequestHeader annotation (binds HTTP headers to method parameters)
import org.springframework.web.bind.annotation.RequestHeader;
// Import for ResponseCookie (creates secure HTTP cookies)
import org.springframework.http.ResponseCookie;
// Import for CacheControl (defines browser caching behavior)
import org.springframework.http.CacheControl;
// Import for ResponseBody annotation (converts return value to JSON)
import org.springframework.web.bind.annotation.ResponseBody;
// Import for RestController annotation (marks class as REST controller)
import org.springframework.web.bind.annotation.RestController;

// Import for List interface (generic collection for lists)
import java.util.List;
// Import for Collections utility (provides static methods for collections)
import java.util.Collections;
// Import for Locale (represents language/region for internationalization)
import java.util.Locale;
// Import for Map interface (key-value pairs)
import java.util.Map;
// Import for LinkedHashMap (preserves insertion order of map entries)
import java.util.LinkedHashMap;
// Import for Callable (enables synchronous operation in asynchronous context)
import java.util.concurrent.Callable;
// Import for CompletableFuture (enables asynchronous, non-blocking operations)
import java.util.concurrent.CompletableFuture;
// Import for CompletionException (thrown on CompletableFuture errors)
import java.util.concurrent.CompletionException;
// Import for Cookie class (represents HTTP cookies)
import jakarta.servlet.http.Cookie;
// Import for HttpServletRequest (represents incoming HTTP request)
import jakarta.servlet.http.HttpServletRequest;
// Import for HttpServletResponse (represents outgoing HTTP response)
import jakarta.servlet.http.HttpServletResponse;
// Import for TypeReference (helps Jackson deserialize generic types)
import com.fasterxml.jackson.core.type.TypeReference;
// Import for ObjectMapper (converts between Java objects and JSON)
import com.fasterxml.jackson.databind.ObjectMapper;

// Import for IOException (thrown on I/O errors)
import java.io.IOException;
// Import for URLDecoder (decodes URL-encoded strings)
import java.net.URLDecoder;
// Import for URLEncoder (encodes strings for URLs)
import java.net.URLEncoder;
// Import for StandardCharsets (provides UTF-8 character encoding)
import java.nio.charset.StandardCharsets;
// Import for ArrayList (dynamic array implementation)
import java.util.ArrayList;
// Import for Duration (represents time duration for cookie expiration)
import java.time.Duration;


// @RestController marks this class as a REST API controller
@RestController
/**
 * SummonerController handles all summoner-related API endpoints.
 * Provides functionality for searching summoners, fetching profiles, match history, and suggestions.
 * Implements search history tracking via cookies for autocomplete functionality.
 */
public class SummonerController {

    // Logger instance for structured log output of this class
    private static final Logger logger = LoggerFactory.getLogger(SummonerController.class);
    // Service instance for Riot API communication (provided via dependency injection)
    private final RiotApiService riotApiService;
    // Service instance for Data Dragon access (champion/item images and data)
    private final DataDragonService dataDragonService;
    // ObjectMapper for JSON serialization/deserialization of cookie data
    private final ObjectMapper objectMapper;
    // Default number of matches per page (loaded from application.properties)
    private final int matchesPageSize;
    // Maximum number of matches that may be loaded per request
    private final int maxMatchesPageSize;
    // Maximum start offset for match pagination (prevents overly large offsets)
    private final int maxMatchesStartOffset;
    // Cookie name for user's search history
    private static final String SEARCH_HISTORY_COOKIE = "searchHistory";
    // Maximum number of entries in search history
    private static final int MAX_HISTORY_SIZE = 10;


    // @Autowired ensures automatic dependency injection by Spring
    @Autowired
    /**
     * Constructor with dependency injection for all required services and configuration values.
     *
     * @param riotApiService Service for Riot API access
     * @param dataDragonService Service for champion/item data
     * @param objectMapper JSON mapper for cookie serialization
     * @param matchesPageSize Page size from config (default: 10)
     * @param maxMatchesPageSize Max page size (default: 40)
     * @param maxMatchesStartOffset Max offset (default: 1000)
     */
    public SummonerController(RiotApiService riotApiService,
                              DataDragonService dataDragonService,
                              ObjectMapper objectMapper,
                              @Value("${ui.matches.page-size:10}") int matchesPageSize,
                              @Value("${ui.matches.max-page-size:40}") int maxMatchesPageSize,
                              @Value("${ui.matches.max-start-offset:1000}") int maxMatchesStartOffset) {
        // Assign RiotApiService to instance variable
        this.riotApiService = riotApiService;
        // Assign DataDragonService to instance variable
        this.dataDragonService = dataDragonService;
        // Assign ObjectMapper to instance variable
        this.objectMapper = objectMapper;
        // Set match page size with validation (minimum 1, otherwise 10)
        this.matchesPageSize = matchesPageSize > 0 ? matchesPageSize : 10;
        // Calculate larger value between matchesPageSize and maxMatchesPageSize
        int pageLimit = Math.max(this.matchesPageSize, maxMatchesPageSize);
        // Set maximum page size with validation (minimum matchesPageSize)
        this.maxMatchesPageSize = pageLimit > 0 ? pageLimit : this.matchesPageSize;
        // Set maximum start offset (minimum 0)
        this.maxMatchesStartOffset = Math.max(0, maxMatchesStartOffset);
    }


    // @GetMapping defines HTTP GET endpoint at /api/matches
    @GetMapping("/api/matches")
    // @ResponseBody ensures automatic JSON serialization of return value
    @ResponseBody
    /**
     * Loads more matches with pagination (asynchronous via CompletableFuture).
     *
     * @param riotId Riot ID in format Name#TAG (required)
     * @param start Start index for pagination (default: 0)
     * @param count Number of matches to load (default: 10)
     * @return CompletableFuture with ResponseEntity containing match list or error
     */
    public CompletableFuture<ResponseEntity<?>> getMoreMatches(@RequestParam("riotId") String riotId,
                                                               @RequestParam(value = "start", defaultValue = "0") int start,
                                                               @RequestParam(value = "count", defaultValue = "10") int count) {
        // Trim the Riot ID and remove leading/trailing spaces
        String trimmedRiotId = riotId != null ? riotId.trim() : null;
        // Validation: check if Riot ID is present and in correct format (must contain #)
        if (!StringUtils.hasText(trimmedRiotId) || !trimmedRiotId.contains("#")) {
            // Return 400 Bad Request with error message (no cache)
            return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // Prevents browser caching of error response
                    .body(Map.of("error", "Invalid riotId format. Expected Name#TAG"))); // JSON error message
        }

        // Split Riot ID at # character (maximum 2 parts)
        String[] parts = trimmedRiotId.split("#", 2);
        // Extract player name (part before #)
        String gameName = parts[0].trim();
        // Extract tagline (part after #)
        String tagLine = parts[1].trim();
        // Validation: check if name and tagline are not empty
        if (!StringUtils.hasText(gameName) || !StringUtils.hasText(tagLine)) {
            // Return 400 Bad Request on empty name/tagline
            return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // No browser caching
                    .body(Map.of("error", "Invalid riotId format. Expected Name#TAG"))); // Error message
        }
        // Validation: count must be positive
        if (count <= 0) {
            // Return 400 Bad Request on negative or zero count
            return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // No browser caching
                    .body(Map.of("error", "Parameter 'count' must be positive."))); // Error message
        }
        // Validation: count must not exceed maxMatchesPageSize
        if (count > maxMatchesPageSize) {
            // Return 400 Bad Request if too many matches requested
            return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // No browser caching
                    .body(Map.of("error", "Maximum matches per request is " + maxMatchesPageSize + "."))); // Error with limit
        }

        // Sanitize start parameter (minimum 0, no negative values)
        final int sanitizedStart = Math.max(0, start);
        // Validation: start must not exceed maxMatchesStartOffset (if configured)
        if (maxMatchesStartOffset > 0 && sanitizedStart > maxMatchesStartOffset) {
            // Return 400 Bad Request on offset too large
            return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // No browser caching
                    .body(Map.of("error", "Parameter 'start' exceeds allowed offset of " + maxMatchesStartOffset + "."))); // Error message
        }

        // Asynchronous call: fetch summoner data by Riot ID
        return riotApiService.getSummonerByRiotId(gameName, tagLine)
                // After successfully loading summoner: load match history
                .thenCompose(summoner -> {
                    // Validation: check if summoner was found and has PUUID
                    if (summoner == null || !StringUtils.hasText(summoner.getPuuid())) {
                        // Return 404 Not Found if summoner doesn't exist
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .cacheControl(CacheControl.noStore()) // No browser caching
                                .body(Map.of("error", "Summoner not found."))); // Error message
                    }
                    // Asynchronous call: load match history with pagination
                    return riotApiService.getMatchHistoryPaged(summoner.getPuuid(), sanitizedStart, count)
                            // After successful loading: create response with match list
                            .thenApply(list -> ResponseEntity.ok() // HTTP 200 OK status
                                    .cacheControl(CacheControl.noStore()) // No browser caching (live data)
                                    .body(list != null ? list : Collections.emptyList())); // Return list (empty if null)
                })
                // Exception handling for all errors in async chain
                .exceptionally(ex -> {
                    // Extract original exception from CompletionException
                    Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;
                    // Log error with full stack trace
                    logger.error("/api/matches error: {}", cause.getMessage(), cause);
                    // Return 500 Internal Server Error on technical errors
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .cacheControl(CacheControl.noStore()) // No browser caching
                            .body(Map.of("error", "Failed to load matches.")); // Generic error message
                });
    }


    // @GetMapping defines HTTP GET endpoint at /api/summoner-suggestions
    @GetMapping("/api/summoner-suggestions")
    /**
     * Provides summoner search suggestions based on search history and query.
     *
     * @param query User's search query
     * @param request HTTP request for cookie access
     * @return ResponseEntity with list of suggestions
     */
    public ResponseEntity<List<SummonerSuggestionDTO>> summonerSuggestions(@RequestParam("query") String query,
                                                                             HttpServletRequest request) {
        // Load search history from user's browser cookie
        Map<String, SummonerSuggestionDTO> userHistory = getSearchHistoryFromCookie(request);
        // Get search suggestions from service (combines history + new search)
        List<SummonerSuggestionDTO> list = riotApiService.getSummonerSuggestions(query, userHistory);
        // Return suggestion list with HTTP 200 OK status
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore()) // No browser caching (live data)
                .body(list); // List of suggestions as JSON response
    }


    // @GetMapping defines HTTP GET endpoint at /api/me (for OAuth authentication)
    @GetMapping("/api/me")
    // @ResponseBody ensures automatic JSON serialization of return value
    @ResponseBody
    /**
     * Fetches the user's own summoner profile via RSO (Riot Sign-On) token.
     *
     * @param authorizationHeader Authorization header with Bearer token (optional)
     * @return Callable with ResponseEntity containing summoner data or error
     */
    public Callable<ResponseEntity<?>> getMySummoner(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        // Validation: check if Authorization header is present and starts with "Bearer "
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            // Return lambda function (Callable) with 401 Unauthorized
            return () -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noStore()) // No browser caching
                    .body(Map.of("error", "Missing or invalid Authorization header. Expected 'Bearer <token>'.")); // Error message
        }

        // Extract Bearer token from Authorization header
        String bearerToken = authorizationHeader.substring("Bearer ".length()).trim();
        // Validation: check if token is not empty after trimming
        if (!StringUtils.hasText(bearerToken)) {
            // Return lambda function (Callable) with 401 Unauthorized on empty token
            return () -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noStore()) // No browser caching
                    .body(Map.of("error", "Empty bearer token.")); // Error message
        }

        // Return lambda function (Callable) for asynchronous processing
        return () -> {
            // Try block for exception handling
            try {
                // Asynchronous call: fetch summoner via RSO token (join() blocks until done)
                Summoner summoner = riotApiService.getSummonerViaRso(bearerToken).join();
                // Validation: check if summoner was found
                if (summoner == null) {
                    // Return 404 Not Found if token is invalid or summoner doesn't exist
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .cacheControl(CacheControl.noStore()) // No browser caching
                            .body(Map.of("error", "Summoner not found or token invalid.")); // Error message
                }
                // Return summoner object with HTTP 200 OK status
                return ResponseEntity.ok()
                        .cacheControl(CacheControl.noStore()) // No browser caching (sensitive data)
                        .body(summoner); // Summoner data as JSON response
            // Catch block for all exceptions during processing
            } catch (Exception e) {
                // Log error with full stack trace
                logger.error("Error in /api/me endpoint: {}", e.getMessage(), e);
                // Return 500 Internal Server Error on technical errors
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .cacheControl(CacheControl.noStore()) // No browser caching
                        .body(Map.of("error", "Internal error while resolving summoner via RSO.")); // Generic error message
            }
        };
    }


    // @GetMapping defines HTTP GET endpoint at /api/profile (main endpoint for player profiles)
    @GetMapping("/api/profile")
    /**
     * Fetches a complete summoner profile with all details.
     *
     * @param riotId Riot ID in format Name#TAG (required)
     * @param includeMatches Flag whether to include match history (default: true)
     * @param request HTTP request for cookie access
     * @param response HTTP response for setting cookies
     * @param locale User's language/region setting
     * @return ResponseEntity with profile data or error
     */
    public ResponseEntity<?> getSummonerProfile(@RequestParam("riotId") String riotId,
                                                @RequestParam(value = "includeMatches", defaultValue = "true") boolean includeMatches,
                                                HttpServletRequest request,
                                                HttpServletResponse response,
                                                Locale locale) {
        // Normalize Riot ID by trimming leading/trailing spaces
        String normalizedRiotId = riotId != null ? riotId.trim() : null;
        // Validation: check if Riot ID is present and in correct format (must contain #)
        if (!StringUtils.hasText(normalizedRiotId) || !normalizedRiotId.contains("#")) {
            // Return 400 Bad Request on invalid format
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // No browser caching
                    .body(Map.of("error", "Invalid Riot ID. Please use the format Name#TAG.")); // Error message
        }

        // Split Riot ID at # character (maximum 2 parts)
        String[] parts = normalizedRiotId.split("#", 2);
        // Extract player name (part before #)
        String gameName = parts[0].trim();
        // Extract tagline (part after #)
        String tagLine = parts[1].trim();

        // Validation: check if name and tagline are not empty
        if (!StringUtils.hasText(gameName) || !StringUtils.hasText(tagLine)) {
            // Return 400 Bad Request on empty name/tagline
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // No browser caching
                    .body(Map.of("error", "Invalid Riot ID. Name and Tagline cannot be empty.")); // Error message
        }

        // Try block for exception handling during profile data fetch
        try {
            // Asynchronous call: fetch complete profile data (join() blocks until done)
            SummonerProfileData profileData = riotApiService.getSummonerProfileDataAsync(gameName, tagLine).join();
            // Validation: check if profile data was found
            if (profileData == null) {
                // Return 404 Not Found if summoner doesn't exist
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .cacheControl(CacheControl.noStore()) // No browser caching
                        .body(Map.of("error", "Summoner not found.")); // Error message
            }

            // Validation: check if profile data contains an error (e.g. Riot API error)
            if (profileData.hasError()) {
                // Return 200 OK with error message (not a server error, but data error)
                return ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noStore()) // No browser caching
                        .body(Map.of("error", profileData.errorMessage())); // Error text from profile data
            }

            // Create LinkedHashMap for response payload (preserves order)
            Map<String, Object> payload = new LinkedHashMap<>();
            // Add summoner object to payload
            payload.put("summoner", profileData.summoner());
            // Add suggestion object (for autocomplete/search history)
            payload.put("suggestion", profileData.suggestion());
            // Add league entries (Solo/Duo, Flex, etc.)
            payload.put("leagueEntries", profileData.leagueEntries());
            // Add champion play statistics (how often each champion was played)
            payload.put("championPlayCounts", profileData.championPlayCounts());
            // Add profile icon URL (player's profile picture)
            payload.put("profileIconUrl", profileData.profileIconUrl());
            // Add Riot ID (from suggestion if available, otherwise normalized ID)
            payload.put("riotId", profileData.suggestion() != null ? profileData.suggestion().getRiotId() : normalizedRiotId);
            // Add configured match page size
            payload.put("matchesPageSize", matchesPageSize);
            // Conditionally add match history (only if includeMatches parameter = true)
            if (includeMatches) {
                // Add match history to payload
                payload.put("matchHistory", profileData.matchHistory());
            }

            // Try block for optional loading of Data Dragon base URLs
            try {
                // Get image base URLs from Data Dragon service (for champion/item images)
                Map<String, String> bases = dataDragonService.getImageBases(null);
                // Validation: only add if base URLs are present
                if (bases != null && !bases.isEmpty()) {
                    // Add base URLs to payload
                    payload.put("bases", bases);
                }
            // Catch block for errors loading base URLs (not critical, so only warning)
            } catch (Exception ex) {
                // Log warning (not an error since data is optional)
                logger.warn("Failed to load DDragon base URLs: {}", ex.getMessage());
            }
            // Try block for optional loading of champion square URLs
            try {
                // Get champion square URLs (champion images mapped by key ID)
                Map<Integer, String> championSquares = dataDragonService.getChampionKeyToSquareUrl(locale);
                // Validation: only add if champion URLs are present
                if (championSquares != null && !championSquares.isEmpty()) {
                    // Add champion square URLs to payload
                    payload.put("championSquares", championSquares);
                }
            // Catch block for errors loading champion URLs (not critical, so only warning)
            } catch (Exception ex) {
                // Log warning (not an error since data is optional)
                logger.warn("Failed to load champion square URLs: {}", ex.getMessage());
            }

            // Conditionally update search history cookie (only if suggestion is present)
            if (profileData.suggestion() != null) {
                // Update search history cookie with current search
                updateSearchHistoryCookie(request, response, normalizedRiotId, profileData.suggestion());
            }

            // Return complete payload with HTTP 200 OK status
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore()) // No browser caching (live data)
                    .body(payload); // Payload with all profile data as JSON response
        // Catch block for CompletionException (async errors)
        } catch (CompletionException ex) {
            // Extract original exception from CompletionException
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            // Log error with Riot ID and full stack trace
            logger.error("Error processing summoner profile for Riot ID '{}': {}", normalizedRiotId, cause.getMessage(), cause);
            // Return 500 Internal Server Error on async errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore()) // No browser caching
                    .body(Map.of("error", "Failed to load summoner profile.")); // Generic error message
        // Catch block for all other unexpected exceptions
        } catch (Exception ex) {
            // Log error with Riot ID and full stack trace
            logger.error("Unexpected error during summoner profile for Riot ID '{}': {}", normalizedRiotId, ex.getMessage(), ex);
            // Return 500 Internal Server Error on unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore()) // No browser caching
                    .body(Map.of("error", "Unexpected error while fetching summoner profile.")); // Generic error message
        }
    }


    /**
     * Private helper method to load search history from browser cookie.
     *
     * @param request HTTP request to access cookies
     * @return Map of search history or empty map if not found
     */
    private Map<String, SummonerSuggestionDTO> getSearchHistoryFromCookie(HttpServletRequest request) {
        // Get all cookies from request (null-safe)
        Cookie[] cookies = request != null ? request.getCookies() : null;

        // Validation: check if cookies are present
        if (cookies != null) {
            // Iterate over all cookies in request
            for (Cookie cookie : cookies) {
                // Check if current cookie is the search history cookie
                if (SEARCH_HISTORY_COOKIE.equals(cookie.getName())) {
                    // Try block for JSON deserialization
                    try {
                        // URL-decode cookie value (UTF-8 encoding)
                        String decodedValue = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.name());
                        // Deserialize JSON string back to Map (with TypeReference for generics)
                        return objectMapper.readValue(decodedValue, new TypeReference<LinkedHashMap<String, SummonerSuggestionDTO>>() {});
                    // Catch block for I/O and parsing errors
                    } catch (IOException | IllegalArgumentException e) {
                        // Log error (cookie might be corrupted)
                        logger.error("Error reading search history cookie: {}", e.getMessage(), e);
                        // Return empty map on error
                        return new LinkedHashMap<>();
                    }
                }
            }
        }
        // Return empty map if no cookie was found
        return new LinkedHashMap<>();
    }


    /**
     * Private helper method to update search history in browser cookie.
     *
     * @param request HTTP request for cookie access
     * @param response HTTP response for setting cookie
     * @param riotId Riot ID of searched player
     * @param suggestionDTO Suggestion object with profile data
     */
    private void updateSearchHistoryCookie(HttpServletRequest request,
                                           HttpServletResponse response,
                                           String riotId,
                                           SummonerSuggestionDTO suggestionDTO) {
        // Normalize Riot ID by trimming leading/trailing spaces
        String normalizedRiotId = riotId != null ? riotId.trim() : null;
        // Validation: check if Riot ID and suggestion object are present
        if (!StringUtils.hasText(normalizedRiotId) || suggestionDTO == null) {
            // Early abort if data is invalid
            return;
        }

        // Load existing search history from cookie
        Map<String, SummonerSuggestionDTO> history = getSearchHistoryFromCookie(request);

        // Normalize key to lowercase (case-insensitive)
        String normalizedKey = normalizedRiotId.toLowerCase(Locale.ROOT);
        // Remove old entry (if present) to avoid duplicates
        history.remove(normalizedKey);
        // Add new entry at end of map (newest last)
        history.put(normalizedKey, suggestionDTO);

        // Limit history size to MAX_HISTORY_SIZE (FIFO principle)
        while (history.size() > MAX_HISTORY_SIZE) {
            // Get oldest key (first entry in LinkedHashMap)
            String oldestKey = history.keySet().iterator().next();
            // Remove oldest entry
            history.remove(oldestKey);
        }

        // Try block for JSON serialization and cookie creation
        try {
            // Serialize history map to JSON string
            String jsonHistory = objectMapper.writeValueAsString(history);
            // URL-encode JSON string (UTF-8 encoding for cookie compatibility)
            String encodedValue = URLEncoder.encode(jsonHistory, StandardCharsets.UTF_8.name());
            // Check if request came via HTTPS (for Secure cookie flag)
            boolean secure = request.isSecure();
            // If not secure, check if behind proxy (X-Forwarded-Proto header)
            if (!secure) {
                // Get X-Forwarded-Proto header (set by reverse proxies)
                String forwardedProto = request.getHeader("X-Forwarded-Proto");
                // Validation: check if header is present
                if (StringUtils.hasText(forwardedProto)) {
                    // Set secure = true if proxy uses HTTPS
                    secure = forwardedProto.trim().equalsIgnoreCase("https");
                }
            }

            // Create ResponseCookie with all security flags
            ResponseCookie cookie = ResponseCookie.from(SEARCH_HISTORY_COOKIE, encodedValue) // Cookie name and value
                    .httpOnly(true) // Prevents JavaScript access (XSS protection)
                    .secure(secure) // Only send over HTTPS (if HTTPS active)
                    .path("/") // Cookie valid for entire domain
                    .sameSite("Lax") // CSRF protection (send cookie only on navigation)
                    .maxAge(Duration.ofDays(30)) // Cookie expiration: 30 days
                    .build(); // Create cookie object
            // Add Set-Cookie header to response
            response.addHeader("Set-Cookie", cookie.toString());
        // Catch block for serialization and encoding errors
        } catch (IOException e) {
            // Log error (cookie update is not critical)
            logger.error("Error writing search history cookie: " + e.getMessage(), e);
        }
    }
// End of class
}
