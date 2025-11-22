// Package declaration - defines the namespace for the controller class
package com.zerox80.riotapi.controller;

// Import for ChampionBuildDto (Data Transfer Object for champion build data)
import com.zerox80.riotapi.dto.ChampionBuildDto;
// Import for BuildAggregationService (service for aggregating build data)
import com.zerox80.riotapi.service.BuildAggregationService;
// Import for Value annotation (injects configuration values from application.properties)
import org.springframework.beans.factory.annotation.Value;
// Import for HttpStatus enum (defines HTTP status codes like 200, 403, 404)
import org.springframework.http.HttpStatus;
// Import for ResponseEntity (enables HTTP status code and header control)
import org.springframework.http.ResponseEntity;
// Import for StringUtils (helps with string validation and manipulation)
import org.springframework.util.StringUtils;
// Import for various web annotations (REST controller and mappings)
import org.springframework.web.bind.annotation.*;

// Import for HttpServletRequest (represents incoming HTTP request)
import jakarta.servlet.http.HttpServletRequest;

// Import for Locale (represents language/region for internationalization)
import java.util.Locale;

// @RestController marks this class as a REST API controller
@RestController
// @RequestMapping defines base path for all endpoints in this class
@RequestMapping("/api")
/**
 * BuildController handles champion build-related API endpoints.
 * Provides functionality for fetching champion builds and triggering build
 * aggregation.
 * Admin-protected aggregation endpoint requires token authentication.
 */
public class BuildController {

    // Service instance for build aggregation (provided via dependency injection)
    private final BuildAggregationService agg;

    // Configuration value: enables or disables the aggregation trigger endpoint
    @Value("${build.agg.trigger-enabled:false}") // Default: false (disabled)
    private boolean triggerEnabled;

    // Configuration value: secret token for accessing the aggregation endpoint
    @Value("${build.agg.trigger-token:}") // Default: empty (no token configured)
    private String triggerToken;

    /**
     * Constructor with dependency injection for BuildAggregationService.
     *
     * @param agg Service for build aggregation
     */
    public BuildController(BuildAggregationService agg) {
        // Assign BuildAggregationService to instance variable
        this.agg = agg;
    }

    // @GetMapping defines HTTP GET endpoint at /api/champions/{id}/build
    @GetMapping("/champions/{id}/build")
    /**
     * Fetches champion build data for a specific champion.
     *
     * @param id      Champion ID from URL path (e.g. "Ahri")
     * @param queueId Queue ID (optional, e.g. 420 for Solo/Duo)
     * @param role    Role (optional, e.g. "MIDDLE")
     * @param locale  User's language/region setting
     * @return ResponseEntity with build data
     */
    public ResponseEntity<ChampionBuildDto> getBuild(@PathVariable("id") String id,
            @RequestParam(value = "queueId", required = false) Integer queueId,
            @RequestParam(value = "role", required = false) String role,
            Locale locale) {
        // Load champion build from service (filters by champion, queue, and role)
        ChampionBuildDto dto = agg.loadBuild(id, queueId, role, locale);
        // Return build DTO with HTTP 200 OK status
        return ResponseEntity.ok(dto); // Build data as JSON response
    }

    // @PostMapping defines HTTP POST endpoint at /api/champions/{id}/aggregate
    @PostMapping("/champions/{id}/aggregate")
    /**
     * Starts build aggregation for a specific champion (admin function).
     * Requires X-Aggregation-Token header for authentication.
     *
     * @param id                 Champion ID from URL path (e.g. "Ahri")
     * @param queueId            Queue ID (optional, e.g. 420 for Solo/Duo)
     * @param pages              Number of summoner pages to search (default: 1)
     * @param matchesPerSummoner Matches per summoner (default: 8)
     * @param maxSummoners       Maximum number of summoners (default: 75)
     * @param locale             User's language/region setting
     * @param request            HTTP request for header access
     * @return ResponseEntity with status message
     */
    public ResponseEntity<String> aggregate(@PathVariable("id") String id,
            @RequestParam(value = "queueId", required = false) Integer queueId,
            @RequestParam(value = "pages", defaultValue = "1") int pages,
            @RequestParam(value = "matchesPerSummoner", defaultValue = "8") int matchesPerSummoner,
            @RequestParam(value = "maxSummoners", defaultValue = "75") int maxSummoners,
            Locale locale,
            HttpServletRequest request) {
        // Validation: check if aggregation trigger is enabled
        if (!triggerEnabled) {
            // Return 403 Forbidden if feature is disabled
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Aggregation trigger is disabled");
        }
        // Validation: check if token is configured
        if (!StringUtils.hasText(triggerToken)) {
            // Return 403 Forbidden if no token is configured
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Aggregation trigger token is not configured");
        }

        // Get X-Aggregation-Token header from request
        String providedToken = request.getHeader("X-Aggregation-Token");
        // Validation: check if provided token matches configured token using
        // constant-time comparison
        if (providedToken == null
                || !java.security.MessageDigest.isEqual(triggerToken.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        providedToken.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            // Return 403 Forbidden on invalid token
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid aggregation trigger token");
        }
        // Validation: check if queue ID is supported (only 420 and 440 allowed)
        if (queueId != null && queueId != 420 && queueId != 440) {
            // Return 400 Bad Request on unsupported queue
            return ResponseEntity.badRequest()
                    .body("Unsupported queueId. Allowed values are 420 (Solo/Duo) or 440 (Flex).");
        }
        // Set effective queue ID (default: 420 if not specified)
        Integer effectiveQueueId = (queueId != null) ? queueId : 420;
        // Start asynchronous aggregation for the champion (runs in background)
        agg.aggregateChampion(id, effectiveQueueId, pages, matchesPerSummoner, maxSummoners, locale);
        // Return 202 Accepted status (request accepted, processing ongoing)
        return ResponseEntity.accepted().body("Aggregation started for " + id); // Confirmation message
    }
}
