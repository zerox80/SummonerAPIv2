// Package declaration - defines the namespace for the controller class
package com.zerox80.riotapi.controller;

// Import for ChampionDetail (model with detailed champion information)
import com.zerox80.riotapi.model.ChampionDetail;
// Import for ChampionSummary (model with champion overview data)
import com.zerox80.riotapi.model.ChampionSummary;
// Import for DataDragonService (service for Data Dragon API access)
import com.zerox80.riotapi.service.DataDragonService;
// Import for CacheControl (defines browser caching behavior)
import org.springframework.http.CacheControl;
// Import for HttpStatus enum (defines HTTP status codes like 200, 404, 500)
import org.springframework.http.HttpStatus;
// Import for ResponseEntity (enables HTTP status code and header control)
import org.springframework.http.ResponseEntity;
// Import for GetMapping annotation (defines HTTP GET endpoints)
import org.springframework.web.bind.annotation.GetMapping;
// Import for PathVariable annotation (binds URL path variables to parameters)
import org.springframework.web.bind.annotation.PathVariable;
// Import for RequestMapping annotation (defines base path for controller)
import org.springframework.web.bind.annotation.RequestMapping;
// Import for RestController annotation (marks class as REST controller)
import org.springframework.web.bind.annotation.RestController;

// Import for SLF4J Logger (logging framework for structured logs)
import org.slf4j.Logger;
// Import for LoggerFactory (creates Logger instances)
import org.slf4j.LoggerFactory;
// Import for Collections utility (provides static methods for collections)
import java.util.Collections;
// Import for List interface (generic collection for lists)
import java.util.List;
// Import for Locale (represents language/region for internationalization)
import java.util.Locale;
// Import for Duration (represents time duration for cache expiration)
import java.time.Duration;

// @RestController marks this class as a REST API controller
@RestController
// @RequestMapping defines base path (here: no base path, just root)
@RequestMapping
/**
 * ChampionsController handles champion data API endpoints.
 * Provides functionality for fetching champion lists and detailed champion
 * information.
 * Implements caching for improved performance and reduced API calls.
 */
public class ChampionsController {

    // Service instance for Data Dragon access (provided via dependency injection)
    private final DataDragonService dataDragonService;
    // Logger instance for structured log output of this class
    private static final Logger log = LoggerFactory.getLogger(ChampionsController.class);

    /**
     * Constructor with dependency injection for DataDragonService.
     *
     * @param dataDragonService Service for Data Dragon data
     */
    public ChampionsController(DataDragonService dataDragonService) {
        // Assign DataDragonService to instance variable
        this.dataDragonService = dataDragonService;
    }

    // @GetMapping defines HTTP GET endpoint at /api/champions
    @GetMapping("/api/champions")
    /**
     * Fetches a list of all champions with basic information.
     *
     * @param localeParam Optional locale parameter from query string
     * @param locale      Fallback locale from Accept-Language header
     * @return ResponseEntity with list of champion summaries
     */
    public ResponseEntity<List<ChampionSummary>> apiChampions(
            @org.springframework.web.bind.annotation.RequestParam(value = "locale", required = false) String localeParam,
            Locale locale) {
        // Try block for exception handling during champion loading
        try {
            // Fetch champion summaries with resolved locale
            List<ChampionSummary> champions = dataDragonService
                    .getChampionSummaries(resolveLocaleOverride(localeParam, locale));
            // Create Cache-Control header (30 minutes cache, publicly cacheable)
            CacheControl cc = CacheControl.maxAge(Duration.ofMinutes(30)).cachePublic();
            // Return champion list with HTTP 200 OK and cache headers
            return ResponseEntity.ok().cacheControl(cc).body(champions);
            // Catch block for all exceptions during loading
        } catch (Exception e) {
            // Log warning (not error, for graceful degradation)
            log.warn("/api/champions failed: {}", e.toString());
            // Return 500 Internal Server Error instead of masking the issue
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).cacheControl(CacheControl.noStore()).build();
        }
    }

    // @GetMapping defines HTTP GET endpoint at /api/champions/{id}
    @GetMapping("/api/champions/{id}")
    /**
     * Fetches detailed information for a specific champion.
     *
     * @param id          Champion ID from URL path (e.g. "Ahri")
     * @param localeParam Optional locale parameter from query string
     * @param locale      Fallback locale from Accept-Language header
     * @return ResponseEntity with champion details or 404 if not found
     */
    public ResponseEntity<ChampionDetail> apiChampion(
            @PathVariable("id") String id,
            @org.springframework.web.bind.annotation.RequestParam(value = "locale", required = false) String localeParam,
            Locale locale) {
        // Try block for exception handling during champion detail loading
        try {
            // Fetch champion details with resolved locale
            ChampionDetail detail = dataDragonService.getChampionDetail(id, resolveLocaleOverride(localeParam, locale));
            // Validation: check if champion was found
            if (detail == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found if champion doesn't exist
            // Create Cache-Control header (30 minutes cache, publicly cacheable)
            CacheControl cc = CacheControl.maxAge(Duration.ofMinutes(30)).cachePublic();
            // Return champion details with HTTP 200 OK and cache headers
            return ResponseEntity.ok().cacheControl(cc).body(detail);
            // Catch block for all exceptions during loading
        } catch (java.io.IOException e) {
            // Return 404 Not Found if the resource is missing (e.g. file not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).cacheControl(CacheControl.noStore()).build();
        } catch (Exception e) {
            // Return 500 Internal Server Error on technical errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).cacheControl(CacheControl.noStore()).build();
        }
    }

    /**
     * Private helper method to resolve locale parameter with fallback.
     *
     * @param localeParam Locale parameter from query string (optional)
     * @param fallback    Fallback locale if parameter is missing/invalid
     * @return Resolved Locale object
     */
    private Locale resolveLocaleOverride(String localeParam,
            Locale fallback) {
        // Validation: check if locale parameter is empty or missing
        if (localeParam == null || localeParam.isBlank()) {
            // Return fallback locale when parameter is missing
            return fallback;
        }
        // Try block for locale parsing
        try {
            // Convert locale string to Locale object (replaces _ with -)
            return Locale.forLanguageTag(localeParam.replace('_', '-'));
            // Catch block for invalid locale strings
        } catch (Exception ignored) {
            // Return fallback locale on parsing error
            return fallback;
        }
    }
}
