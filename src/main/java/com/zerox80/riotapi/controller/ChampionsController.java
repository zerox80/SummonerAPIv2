package com.zerox80.riotapi.controller;

import com.zerox80.riotapi.model.ChampionDetail;
import com.zerox80.riotapi.model.ChampionSummary;
import com.zerox80.riotapi.service.DataDragonService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.time.Duration;


@RestController
@RequestMapping
public class ChampionsController {

    private final DataDragonService dataDragonService;
    private static final Logger log = LoggerFactory.getLogger(ChampionsController.class);

    
    public ChampionsController(DataDragonService dataDragonService) {
        this.dataDragonService = dataDragonService;
    }
    
    @GetMapping("/api/champions")
    public ResponseEntity<List<ChampionSummary>> apiChampions(
            @org.springframework.web.bind.annotation.RequestParam(value = "locale", required = false) String localeParam,
            Locale locale) {
        try {
            List<ChampionSummary> champions = dataDragonService.getChampionSummaries(resolveLocaleOverride(localeParam, locale));
            CacheControl cc = CacheControl.maxAge(Duration.ofMinutes(30)).cachePublic();
            return ResponseEntity.ok().cacheControl(cc).body(champions);
        } catch (Exception e) {
            log.warn("/api/champions failed: {}", e.toString());
            // Degrade gracefully: return empty list to callers
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Collections.emptyList());
        }
    }

    
    @GetMapping("/api/champions/{id}")
    public ResponseEntity<ChampionDetail> apiChampion(
            @PathVariable("id") String id,
            @org.springframework.web.bind.annotation.RequestParam(value = "locale", required = false) String localeParam,
            Locale locale) {
        try {
            ChampionDetail detail = dataDragonService.getChampionDetail(id, resolveLocaleOverride(localeParam, locale));
            if (detail == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            CacheControl cc = CacheControl.maxAge(Duration.ofMinutes(30)).cachePublic();
            return ResponseEntity.ok().cacheControl(cc).body(detail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).cacheControl(CacheControl.noStore()).build();
        }
    }

    
    private Locale resolveLocaleOverride(String localeParam, Locale fallback) {
        if (localeParam == null || localeParam.isBlank()) {
            return fallback;
        }
        try {
            return Locale.forLanguageTag(localeParam.replace('_', '-'));
        } catch (Exception ignored) {
            return fallback;
        }
    }
}
