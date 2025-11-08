package com.zerox80.riotapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Scheduled service for running nightly champion build aggregations.
 *
 * <p>This service triggers the build aggregation process based on a cron schedule.
 * It is responsible for parsing configuration properties to determine which champions
 * to process and with what parameters.</p>
 *
 * <p>The scheduler can be enabled or disabled via configuration and supports
 * customization of the aggregation parameters such as queue ID, number of pages,
 * and matches per summoner.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Service
public class BuildAggregationScheduler {

    private static final Logger log = LoggerFactory.getLogger(BuildAggregationScheduler.class);

    private final BuildAggregationService agg;

    @Value("${build.agg.enabled:false}")
    private boolean enabled;

    @Value("${build.agg.queue-id:420}")
    private int queueId;

    @Value("${build.agg.pages:1}")
    private int pages;

    @Value("${build.agg.matches-per-summoner:6}")
    private int matchesPerSummoner;

    @Value("${build.agg.max-summoners:50}")
    private int maxSummoners;

    @Value("${build.agg.champions:}")
    private String championsCsv;

    /**
     * Constructs a new BuildAggregationScheduler with the required service dependency.
     *
     * @param agg The build aggregation service.
     */
    public BuildAggregationScheduler(BuildAggregationService agg) {
        this.agg = agg;
    }

    /**
     * Executes the configured nightly champion build aggregation workflow.
     *
     * <p>Triggered by {@link Scheduled} cron expression {@code ${build.agg.cron:0 15 3 * * *}},
     * this method validates that the scheduler is enabled, parses the champion allow list,
     * and invokes {@link BuildAggregationService#aggregateChampion(String, int, int, int, int, Locale)}
     * for each entry with the configured queue, pagination, and sampling limits. The default
     * schedule runs every day at 03:15 UTC but can be overridden via {@code build.agg.cron}.</p>
     *
     * @return Nothing; the method schedules aggregation side effects only
     */
    @Scheduled(cron = "${build.agg.cron:0 15 3 * * *}")
    public void runNightly() {
        if (!enabled) {
            return;
        }
        List<String> champs = parseChampions(championsCsv);
        if (champs.isEmpty()) {
            log.warn("Build aggregation enabled but no champions configured (build.agg.champions). Skipping.");
            return;
        }
        Locale locale = Locale.US; // server-side aggregation locale for static lookups
        for (String champ : champs) {
            try {
                agg.aggregateChampion(champ, queueId, pages, matchesPerSummoner, maxSummoners, locale);
            } catch (Exception e) {
                log.warn("Aggregation for {} failed: {}", champ, e.toString());
            }
        }
    }

    private List<String> parseChampions(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
