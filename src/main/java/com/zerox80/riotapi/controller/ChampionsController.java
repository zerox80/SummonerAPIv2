package com.zerox80.riotapi.controller;

import com.zerox80.riotapi.model.ChampionDetail;
import com.zerox80.riotapi.model.ChampionSummary;
import com.zerox80.riotapi.service.DataDragonService;
import com.zerox80.riotapi.service.BuildAggregationService;
import com.zerox80.riotapi.dto.ChampionBuildDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping
public class ChampionsController {

    private final DataDragonService dataDragonService;
    private final BuildAggregationService buildAggregationService;

    public ChampionsController(DataDragonService dataDragonService,
                               BuildAggregationService buildAggregationService) {
        this.dataDragonService = dataDragonService;
        this.buildAggregationService = buildAggregationService;
    }

    // Page: champions list
    @GetMapping("/champions")
    public String championsPage(Locale locale, Model model) {
        try {
            List<ChampionSummary> champions = dataDragonService.getChampionSummaries(locale);
            Map<String, String> bases = dataDragonService.getImageBases(null);
            model.addAttribute("champions", champions);
            model.addAttribute("bases", bases);
            model.addAttribute("version", bases.get("version"));
            return "champions";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load champions: " + e.getMessage());
            Map<String, String> bases = dataDragonService.getImageBases(null);
            model.addAttribute("bases", bases);
            model.addAttribute("version", bases.get("version"));
            model.addAttribute("champions", Collections.emptyList());
            return "champions";
        }
    }

    // Page: champion detail
    @GetMapping("/champions/{id}")
    public String championDetail(@PathVariable("id") String id, Locale locale, Model model) {
        try {
            ChampionDetail detail = dataDragonService.getChampionDetail(id, locale);
            if (detail == null) {
                model.addAttribute("error", "Champion not found");
            }
            Map<String, String> bases = dataDragonService.getImageBases(null);
            model.addAttribute("champion", detail);
            model.addAttribute("bases", bases);
            model.addAttribute("version", bases.get("version"));
            // Load aggregated build data if available
            try {
                ChampionBuildDto build = buildAggregationService.loadBuild(id, null, "ALL", locale);
                model.addAttribute("build", build);
            } catch (Exception ignore) {}
            return "champion";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load champion: " + e.getMessage());
            Map<String, String> bases = dataDragonService.getImageBases(null);
            model.addAttribute("bases", bases);
            model.addAttribute("version", bases.get("version"));
            model.addAttribute("champion", null);
            return "champion";
        }
    }

    // API: champions list
    @GetMapping("/api/champions")
    @ResponseBody
    public ResponseEntity<List<ChampionSummary>> apiChampions(Locale locale) {
        try {
            List<ChampionSummary> champions = dataDragonService.getChampionSummaries(locale);
            return ResponseEntity.ok(champions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // API: champion detail
    @GetMapping("/api/champions/{id}")
    @ResponseBody
    public ResponseEntity<ChampionDetail> apiChampion(@PathVariable("id") String id, Locale locale) {
        try {
            ChampionDetail detail = dataDragonService.getChampionDetail(id, locale);
            if (detail == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
