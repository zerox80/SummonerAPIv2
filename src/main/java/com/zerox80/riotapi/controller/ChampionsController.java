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

/**
 * REST controller for handling champion-related API endpoints.
 * 
 * <p>This controller provides endpoints for retrieving champion data from Data Dragon,
 * including champion summaries and detailed champion information. It serves as the
 * backend for the champion browser and build statistics features.</p>
 * 
 * <p>Endpoints provided:</p>
 * <ul>
 *   <li>GET /api/champions - List of all champions with basic information</li>
 *   <li>GET /api/champions/{id} - Detailed champion information</li>
 * </ul>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Response caching for performance (30 minutes)</li>
 *   <li>Locale support for internationalization</li>
 *   <li>Graceful error handling with empty list fallbacks</li>
 *   <li>Integration with Data Dragon service</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@RestController
@RequestMapping
public class ChampionsController {

    private final DataDragonService dataDragonService;
    private static final Logger log = LoggerFactory.getLogger(ChampionsController.class);

    /**
     * Constructs a new ChampionsController with the required dependencies.
     * 
     * @param dataDragonService Service for accessing Data Dragon champion data
     */
    public ChampionsController(DataDragonService dataDragonService) {
        this.dataDragonService = dataDragonService;
    }
    /**
     * Retrieves a list of all champions with their basic information.
     * 
     * <p>This endpoint returns champion summaries including names, titles,
     * roles, and basic statistics. The response is cached for 30 minutes
     * to improve performance and reduce Data Dragon API calls.</p>
     * 
     * @param localeParam Optional locale parameter to override request locale (e.g., 'en_US', 'de_DE')
     * @param locale The locale from the request headers
     * @return ResponseEntity containing the list of champion summaries
     * @throws Exception If Data Dragon service fails, returns empty list as fallback
     */
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

    /**
     * Retrieves detailed information for a specific champion.
     * 
     * <p>This endpoint returns comprehensive champion data including abilities,
     * stats, lore, and build recommendations. The response is cached for
     * 30 minutes to improve performance.</p>
     * 
     * @param id The champion ID or key (e.g., 'Ahri', '1')
     * @param localeParam Optional locale parameter to override request locale
     * @param locale The locale from the request headers
     * @return ResponseEntity containing the detailed champion information
     * @throws Exception If champion is not found or service fails, returns 404
     */
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
