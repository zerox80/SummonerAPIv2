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


@RestController
@RequestMapping("/api")
public class BuildController {

    
    private final BuildAggregationService agg;

    
    @Value("${build.agg.trigger-enabled:false}")
    private boolean triggerEnabled;

    
    @Value("${build.agg.trigger-token:}")
    private String triggerToken;

    
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
