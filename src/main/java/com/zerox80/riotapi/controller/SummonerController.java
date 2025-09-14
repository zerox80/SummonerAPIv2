package com.zerox80.riotapi.controller;

import com.zerox80.riotapi.model.Summoner;
import com.zerox80.riotapi.model.LeagueEntryDTO;
import com.zerox80.riotapi.model.MatchV5Dto;
import com.zerox80.riotapi.model.SummonerSuggestionDTO;
import com.zerox80.riotapi.service.RiotApiService;
import com.zerox80.riotapi.service.DataDragonService;
import com.zerox80.riotapi.model.SummonerProfileData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseCookie;

import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.time.Duration;

@Controller
public class SummonerController {

    private static final Logger logger = LoggerFactory.getLogger(SummonerController.class);
    private final RiotApiService riotApiService;
    private final DataDragonService dataDragonService;
    private final ObjectMapper objectMapper;
    private static final String SEARCH_HISTORY_COOKIE = "searchHistory";
    private static final int MAX_HISTORY_SIZE = 10;

    @Autowired
    public SummonerController(RiotApiService riotApiService, DataDragonService dataDragonService, ObjectMapper objectMapper) {
        this.riotApiService = riotApiService;
        this.dataDragonService = dataDragonService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String index(Model model, java.util.Locale locale) {
        // Ensure all template variables exist to prevent Thymeleaf errors on first load
        model.addAttribute("summoner", null);
        model.addAttribute("leagueEntries", Collections.emptyList());
        model.addAttribute("matchHistory", Collections.emptyList());
        model.addAttribute("championPlayCounts", Collections.emptyMap());
        model.addAttribute("matchHistoryInfo", null);
        model.addAttribute("leagueError", null);
        model.addAttribute("matchHistoryError", null);
        model.addAttribute("error", null);
        // Provide DDragon image base URLs (for champion icons, etc.)
        model.addAttribute("bases", dataDragonService.getImageBases(null));
        try {
            model.addAttribute("champImgByKey", dataDragonService.getChampionKeyToSquareUrl(locale));
        } catch (Exception ignore) {
            model.addAttribute("champImgByKey", java.util.Collections.emptyMap());
        }
        return "index";
    }

    @GetMapping("/api/summoner-suggestions")
    @ResponseBody
    public List<SummonerSuggestionDTO> summonerSuggestions(@RequestParam("query") String query, HttpServletRequest request) {
        Map<String, SummonerSuggestionDTO> userHistory = getSearchHistoryFromCookie(request);
        return riotApiService.getSummonerSuggestions(query, userHistory);
    }

    @GetMapping("/api/me")
    @ResponseBody
    public Callable<ResponseEntity<?>> getMySummoner(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            return () -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Missing or invalid Authorization header. Expected 'Bearer <token>'."));
        }

        String bearerToken = authorizationHeader.substring("Bearer ".length()).trim();
        if (!StringUtils.hasText(bearerToken)) {
            return () -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Empty bearer token."));
        }

        return () -> {
            try {
                Summoner summoner = riotApiService.getSummonerViaRso(bearerToken).join();
                if (summoner == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Summoner not found or token invalid."));
                }
                return ResponseEntity.ok(summoner);
            } catch (Exception e) {
                logger.error("Error in /api/me endpoint: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Internal error while resolving summoner via RSO."));
            }
        };
    }

    @RequestMapping(value = "/search", method = {RequestMethod.GET, RequestMethod.POST})
    public Callable<String> searchSummoner(@RequestParam("riotId") String riotId, Model model, HttpServletRequest request, HttpServletResponse response, java.util.Locale locale) {
        if (!StringUtils.hasText(riotId) || !riotId.contains("#")) {
            model.addAttribute("error", "Invalid Riot ID. Please use the format Name#TAG.");
            return () -> "index";
        }

        String[] parts = riotId.split("#", 2);
        String gameName = parts[0];
        String tagLine = parts[1];

        if (!StringUtils.hasText(gameName) || !StringUtils.hasText(tagLine)) {
            model.addAttribute("error", "Invalid Riot ID. Name and Tagline cannot be empty.");
            return () -> "index";
        }

        return () -> {
            try {
                CompletableFuture<SummonerProfileData> profileDataFuture = riotApiService.getSummonerProfileDataAsync(gameName, tagLine);
                SummonerProfileData profileData = profileDataFuture.join();

                if (profileData.hasError()) {
                    model.addAttribute("error", profileData.errorMessage());
                    model.addAttribute("summoner", null);
                    model.addAttribute("leagueEntries", Collections.emptyList());
                    model.addAttribute("matchHistory", Collections.emptyList());
                    model.addAttribute("championPlayCounts", Collections.emptyMap());
                    model.addAttribute("matchHistoryInfo", profileData.errorMessage());
                    model.addAttribute("bases", dataDragonService.getImageBases(null));
                    try { model.addAttribute("champImgByKey", dataDragonService.getChampionKeyToSquareUrl(locale)); } catch (Exception ignore) { model.addAttribute("champImgByKey", java.util.Collections.emptyMap()); }
                    return "index";
                }

                model.addAttribute("summoner", profileData.summoner());
                model.addAttribute("leagueEntries", profileData.leagueEntries());
                model.addAttribute("matchHistory", profileData.matchHistory());
                model.addAttribute("championPlayCounts", profileData.championPlayCounts());
                model.addAttribute("profileIconUrl", profileData.profileIconUrl());
                model.addAttribute("bases", dataDragonService.getImageBases(null));
                try { model.addAttribute("champImgByKey", dataDragonService.getChampionKeyToSquareUrl(locale)); } catch (Exception ignore) { model.addAttribute("champImgByKey", java.util.Collections.emptyMap()); }

                if (profileData.summoner() != null && profileData.suggestion() != null) {
                    updateSearchHistoryCookie(request, response, riotId, profileData.suggestion());
                }
                
                if (profileData.matchHistory() == null || profileData.matchHistory().isEmpty()) {
                    if (!model.containsAttribute("error")) {
                        model.addAttribute("matchHistoryInfo", "No recent matches found or PUUID not available.");
                    }
                }

            } catch (CompletionException e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                logger.error("Error processing summoner search for Riot ID '{}': {}", riotId, cause.getMessage(), cause);
                model.addAttribute("error", "An error occurred: " + cause.getMessage());
                model.addAttribute("leagueEntries", Collections.emptyList());
                model.addAttribute("matchHistory", Collections.emptyList());
                model.addAttribute("championPlayCounts", Collections.emptyMap());
                model.addAttribute("matchHistoryInfo", "An error occurred while fetching data.");
                model.addAttribute("bases", dataDragonService.getImageBases(null));
                try { model.addAttribute("champImgByKey", dataDragonService.getChampionKeyToSquareUrl(locale)); } catch (Exception ignore) { model.addAttribute("champImgByKey", java.util.Collections.emptyMap()); }
            } catch (Exception e) {
                logger.error("Unexpected error during summoner search for Riot ID '{}': {}", riotId, e.getMessage(), e);
                model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
                model.addAttribute("leagueEntries", Collections.emptyList());
                model.addAttribute("matchHistory", Collections.emptyList());
                model.addAttribute("championPlayCounts", Collections.emptyMap());
                model.addAttribute("matchHistoryInfo", "An unexpected error occurred.");
                model.addAttribute("bases", dataDragonService.getImageBases(null));
                try { model.addAttribute("champImgByKey", dataDragonService.getChampionKeyToSquareUrl(locale)); } catch (Exception ignore) { model.addAttribute("champImgByKey", java.util.Collections.emptyMap()); }
            }
            return "index";
        };
    }

    private Map<String, SummonerSuggestionDTO> getSearchHistoryFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SEARCH_HISTORY_COOKIE.equals(cookie.getName())) {
                    try {
                        String decodedValue = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.name());
                        return objectMapper.readValue(decodedValue, new TypeReference<LinkedHashMap<String, SummonerSuggestionDTO>>() {});
                    } catch (IOException | IllegalArgumentException e) {
                        logger.error("Error reading search history cookie: " + e.getMessage(), e);
                        return new LinkedHashMap<>();
                    }
                }
            }
        }
        return new LinkedHashMap<>();
    }

    private void updateSearchHistoryCookie(HttpServletRequest request, HttpServletResponse response, String riotId, SummonerSuggestionDTO suggestionDTO) {
        Map<String, SummonerSuggestionDTO> history = getSearchHistoryFromCookie(request);

        history.remove(riotId.toLowerCase());
        history.put(riotId.toLowerCase(), suggestionDTO);

        while (history.size() > MAX_HISTORY_SIZE) {
            String oldestKey = history.keySet().iterator().next();
            history.remove(oldestKey);
        }

        try {
            String jsonHistory = objectMapper.writeValueAsString(history);
            String encodedValue = URLEncoder.encode(jsonHistory, StandardCharsets.UTF_8.name());
            ResponseCookie cookie = ResponseCookie.from(SEARCH_HISTORY_COOKIE, encodedValue)
                    .httpOnly(true)
                    .secure(request.isSecure())
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(Duration.ofDays(30))
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());
        } catch (IOException e) {
            logger.error("Error writing search history cookie: " + e.getMessage(), e);
        }
    }
} 
