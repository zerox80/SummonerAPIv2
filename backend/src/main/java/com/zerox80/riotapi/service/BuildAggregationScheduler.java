// Package declaration: Defines that this class belongs to the service layer
package com.zerox80.riotapi.service;

// Import for logging interface
import org.slf4j.Logger;
// Import for logger factory to instantiate loggers
import org.slf4j.LoggerFactory;
// Import for property injection from application.properties
import org.springframework.beans.factory.annotation.Value;
// Import for scheduled task execution (cron jobs)
import org.springframework.scheduling.annotation.Scheduled;
// Import for service component annotation
import org.springframework.stereotype.Service;

// Import for array utility methods
import java.util.Arrays;
// Import for list collections
import java.util.List;
// Import for language/region settings
import java.util.Locale;


/**
 * Scheduler service for automated nightly build aggregation.
 *
 * Runs a scheduled task (default: daily at 3:15 AM) to aggregate champion build
 * data from high-ELO matches. Configuration is controlled via application.properties.
 */
@Service
public class BuildAggregationScheduler {

    // Logger instance for logging in this service
    private static final Logger log = LoggerFactory.getLogger(BuildAggregationScheduler.class);

    // Service for build aggregation business logic
    private final BuildAggregationService agg;

    // Property injection: Reads configuration from application.properties (default: false)
    @Value("${build.agg.enabled:false}")
    private boolean enabled;

    // Property injection: Queue ID for aggregation (default: 420 = SoloQ)
    @Value("${build.agg.queue-id:420}")
    private int queueId;

    // Property injection: Number of pages of player lists per tier/division (default: 1)
    @Value("${build.agg.pages:1}")
    private int pages;

    // Property injection: Matches per summoner (default: 6)
    @Value("${build.agg.matches-per-summoner:6}")
    private int matchesPerSummoner;

    // Property injection: Maximum number of summoners (default: 50)
    @Value("${build.agg.max-summoners:50}")
    private int maxSummoners;

    // Property injection: Comma-separated list of champion IDs (default: empty)
    @Value("${build.agg.champions:}")
    private String championsCsv;


    /**
     * Constructor with dependency injection.
     *
     * @param agg Injected build aggregation service
     */
    public BuildAggregationScheduler(BuildAggregationService agg) {
        this.agg = agg;
    }


    /**
     * Nightly scheduled task for build aggregation.
     *
     * Runs according to the cron expression (default: daily at 3:15 AM).
     * Iterates through configured champions and triggers asynchronous aggregation
     * for each one. If aggregation is disabled or no champions are configured,
     * the task silently skips execution.
     */
    @Scheduled(cron = "${build.agg.cron:0 15 3 * * *}")
    public void runNightly() {
        // Check if aggregation is enabled
        if (!enabled) {
            return;
        }
        // Parse CSV string to champion list
        List<String> champs = parseChampions(championsCsv);
        // Validation: Check if champions are configured
        if (champs.isEmpty()) {
            log.warn("Build aggregation enabled but no champions configured (build.agg.champions). Skipping.");
            return;
        }
        // Server-side aggregation locale for static lookups
        Locale locale = Locale.US;
        // Iterate over all configured champions
        for (String champ : champs) {
            try {
                // Asynchronous call: Start build aggregation for one champion
                agg.aggregateChampion(champ, queueId, pages, matchesPerSummoner, maxSummoners, locale);
            } catch (Exception e) {
                // Log warning on aggregation error (non-critical)
                log.warn("Aggregation for {} failed: {}", champ, e.toString());
            }
        }
    }

    /**
     * Private helper method to parse the champion CSV.
     *
     * @param csv Comma-separated champion IDs (e.g., "Anivia,Ashe,Zed")
     * @return List of trimmed, non-empty champion IDs
     */
    private List<String> parseChampions(String csv) {
        // Return empty list if CSV is null or blank
        if (csv == null || csv.isBlank()) return List.of();
        // Stream over CSV parts (split by comma)
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
