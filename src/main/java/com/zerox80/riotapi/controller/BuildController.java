package com.zerox80.riotapi.controller;

import com.zerox80.riotapi.dto.ChampionBuildDto;
import com.zerox80.riotapi.service.BuildAggregationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * REST controller for managing champion build data and aggregation operations.
 * 
 * <p>This controller provides endpoints for retrieving pre-computed champion builds
 * and triggering manual build aggregation processes. It supports queue-specific builds,
 * role filtering, and internationalization. The aggregation endpoint is protected
 * by configuration and token-based authentication for security.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Retrieve champion builds with queue and role filtering</li>
 *   <li>Internationalization support for localized build data</li>
 *   <li>Manual build aggregation trigger with token protection</li>
 *   <li>Configurable aggregation parameters (pages, matches, summoners)</li>
 *   <li>Security controls to prevent unauthorized aggregation</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@RestController
@RequestMapping("/api")
public class BuildController {

    /** Service for handling champion build aggregation and loading */
    private final BuildAggregationService agg;

    /** Flag indicating if manual aggregation triggers are enabled */
    @Value("${build.agg.trigger-enabled:false}")
    private boolean triggerEnabled;

    /** Authentication token required for manual aggregation triggers */
    @Value("${build.agg.trigger-token:}")
    private String triggerToken;

    /**
     * Constructs a new BuildController with the required BuildAggregationService dependency.
     * 
     * @param agg The BuildAggregationService for build operations
     */
    public BuildController(BuildAggregationService agg) {
        this.agg = agg;
    }

    /**
     * Retrieves champion build data for the specified champion ID.
     * 
     * <p>This endpoint returns pre-computed champion builds including item sets,
     * rune configurations, and summoner spell recommendations. The data can be
     * filtered by queue type and role, and supports internationalization.</p>
     * 
     * @param id The champion ID to retrieve builds for
     * @param queueId Optional queue ID filter (e.g., 420 for Ranked Solo/Duo)
     * @param role Optional role filter (e.g., "top", "jungle", "mid", "adc", "support")
     * @param locale The locale for localized build data
     * @return ResponseEntity containing the champion build data
     */
    @GetMapping("/champions/{id}/build")
    public ResponseEntity<ChampionBuildDto> getBuild(@PathVariable("id") String id,
                                                     @RequestParam(value = "queueId", required = false) Integer queueId,
                                                     @RequestParam(value = "role", required = false) String role,
                                                     Locale locale) {
        ChampionBuildDto dto = agg.loadBuild(id, queueId, role, locale);
        return ResponseEntity.ok(dto);
    }

    /**
     * Triggers manual build aggregation for the specified champion.
     * 
     * <p>This endpoint initiates the build aggregation process that analyzes recent
     * high-level matches to generate optimal champion builds. The endpoint is protected
     * by configuration flags and requires a valid authentication token to prevent
     * unauthorized use. Aggregation parameters can be customized to control the scope
     * of data analysis.</p>
     * 
     * <p>Security requirements:</p>
     * <ul>
     *   <li>Aggregation triggers must be enabled via configuration</li>
     *   <li>Valid trigger token must be provided in X-Aggregation-Token header</li>
     *   <li>Token must match the configured value</li>
     * </ul>
     * 
     * @param id The champion ID to aggregate builds for
     * @param queueId Optional queue ID to filter matches (default: configured queue)
     * @param pages Number of pages of summoners to analyze (default: 1)
     * @param matchesPerSummoner Number of matches per summoner to analyze (default: 8)
     * @param maxSummoners Maximum number of summoners to process (default: 75)
     * @param locale The locale for localized build data
     * @param request The HTTP request containing authentication headers
     * @return ResponseEntity with status message or error details
     */
    @PostMapping("/champions/{id}/aggregate")
    public ResponseEntity<String> aggregate(@PathVariable("id") String id,
                                            @RequestParam(value = "queueId", required = false) Integer queueId,
                                            @RequestParam(value = "pages", defaultValue = "1") int pages,
                                            @RequestParam(value = "matchesPerSummoner", defaultValue = "8") int matchesPerSummoner,
                                            @RequestParam(value = "maxSummoners", defaultValue = "75") int maxSummoners,
                                            Locale locale,
                                            HttpServletRequest request) {
        if (!triggerEnabled) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Aggregation trigger is disabled");
        }
        if (!StringUtils.hasText(triggerToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Aggregation trigger token is not configured");
        }

        String providedToken = request.getHeader("X-Aggregation-Token");
        if (!triggerToken.equals(providedToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid aggregation trigger token");
        }
        if (queueId != null && queueId != 420 && queueId != 440) {
            return ResponseEntity.badRequest().body("Unsupported queueId. Allowed values are 420 (Solo/Duo) or 440 (Flex).");
        }
        Integer effectiveQueueId = (queueId != null) ? queueId : 420;
        agg.aggregateChampion(id, effectiveQueueId, pages, matchesPerSummoner, maxSummoners, locale);
        return ResponseEntity.accepted().body("Aggregation started for " + id);
    }
}
