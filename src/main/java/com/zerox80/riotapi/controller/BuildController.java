package com.zerox80.riotapi.controller;

import com.zerox80.riotapi.dto.ChampionBuildDto;
import com.zerox80.riotapi.service.BuildAggregationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api")
public class BuildController {

    private final BuildAggregationService agg;

    @Value("${build.agg.trigger-enabled:false}")
    private boolean triggerEnabled;

    public BuildController(BuildAggregationService agg) {
        this.agg = agg;
    }

    @GetMapping("/champions/{id}/build")
    public ResponseEntity<ChampionBuildDto> getBuild(@PathVariable("id") String id,
                                                     @RequestParam(value = "queueId", required = false) Integer queueId,
                                                     @RequestParam(value = "role", required = false) String role,
                                                     Locale locale) {
        ChampionBuildDto dto = agg.loadBuild(id, queueId, role, locale);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/champions/{id}/aggregate")
    public ResponseEntity<String> aggregate(@PathVariable("id") String id,
                                            @RequestParam(value = "queueId", required = false) Integer queueId,
                                            @RequestParam(value = "pages", defaultValue = "1") int pages,
                                            @RequestParam(value = "matchesPerSummoner", defaultValue = "8") int matchesPerSummoner,
                                            @RequestParam(value = "maxSummoners", defaultValue = "75") int maxSummoners,
                                            Locale locale) {
        if (!triggerEnabled) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Aggregation trigger is disabled");
        }
        agg.aggregateChampion(id, queueId, pages, matchesPerSummoner, maxSummoners, locale);
        return ResponseEntity.accepted().body("Aggregation started for " + id);
    }
}
