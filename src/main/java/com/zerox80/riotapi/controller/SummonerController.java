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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseCookie;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
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
import java.util.ArrayList;
import java.time.Duration;

@RestController
public class SummonerController {

    private static final Logger logger = LoggerFactory.getLogger(SummonerController.class);
    private final RiotApiService riotApiService;
    private final DataDragonService dataDragonService;
    private final ObjectMapper objectMapper;
    private final int matchesPageSize;
    private final int maxMatchesPageSize;
    private final int maxMatchesStartOffset;
    private static final String SEARCH_HISTORY_COOKIE = "searchHistory";
    private static final int MAX_HISTORY_SIZE = 10;

    @Autowired
    public SummonerController(RiotApiService riotApiService, DataDragonService dataDragonService, ObjectMapper objectMapper,
                              @Value("${ui.matches.page-size:10}") int matchesPageSize,
                              @Value("${ui.matches.max-page-size:40}") int maxMatchesPageSize,
                              @Value("${ui.matches.max-start-offset:1000}") int maxMatchesStartOffset) {
        this.riotApiService = riotApiService;
        this.dataDragonService = dataDragonService;
        this.objectMapper = objectMapper;
        this.matchesPageSize = matchesPageSize > 0 ? matchesPageSize : 10;
        int pageLimit = Math.max(this.matchesPageSize, maxMatchesPageSize);
        this.maxMatchesPageSize = pageLimit > 0 ? pageLimit : this.matchesPageSize;
        this.maxMatchesStartOffset = Math.max(0, maxMatchesStartOffset);
    }

    @GetMapping("/api/matches")
    @ResponseBody
    public ResponseEntity<?> getMoreMatches(@RequestParam("riotId") String riotId,
                                            @RequestParam(value = "start", defaultValue = "0") int start,
                                            @RequestParam(value = "count", defaultValue = "10") int count) {
        try {
            String trimmedRiotId = riotId != null ? riotId.trim() : null;
            if (!StringUtils.hasText(trimmedRiotId) || !trimmedRiotId.contains("#")) {
                return ResponseEntity.badRequest()
                        .cacheControl(CacheControl.noStore())
                        .body(Map.of("error", "Invalid riotId format. Expected Name#TAG"));
            }
            String[] parts = trimmedRiotId.split("#", 2);
            String gameName = parts[0].trim();
            String tagLine = parts[1].trim();
            if (!StringUtils.hasText(gameName) || !StringUtils.hasText(tagLine)) {
                return ResponseEntity.badRequest()
                        .cacheControl(CacheControl.noStore())
                        .body(Map.of("error", "Invalid riotId format. Expected Name#TAG"));
            }
            if (count <= 0) {
                return ResponseEntity.badRequest()
                        .cacheControl(CacheControl.noStore())
                        .body(Map.of("error", "Parameter 'count' must be positive."));
            }
            if (count > maxMatchesPageSize) {
                return ResponseEntity.badRequest()
                        .cacheControl(CacheControl.noStore())
                        .body(Map.of("error", "Maximum matches per request is " + maxMatchesPageSize + "."));
            }
            int sanitizedStart = Math.max(0, start);
            if (maxMatchesStartOffset > 0 && sanitizedStart > maxMatchesStartOffset) {
                return ResponseEntity.badRequest()
                        .cacheControl(CacheControl.noStore())
                        .body(Map.of("error", "Parameter 'start' exceeds allowed offset of " + maxMatchesStartOffset + "."));
            }
            Summoner summoner = riotApiService.getSummonerByRiotId(gameName, tagLine).join();
            if (summoner == null || !StringUtils.hasText(summoner.getPuuid())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .cacheControl(CacheControl.noStore())
                        .body(Map.of("error", "Summoner not found."));
            }
            List<MatchV5Dto> list = riotApiService.getMatchHistoryPaged(summoner.getPuuid(), sanitizedStart, count).join();
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .body(list != null ? list : Collections.emptyList());
        } catch (Exception e) {
            logger.error("/api/matches error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore())
                    .body(Map.of("error", "Failed to load matches."));
        }
    }

    @GetMapping("/api/summoner-suggestions")
    public ResponseEntity<List<SummonerSuggestionDTO>> summonerSuggestions(@RequestParam("query") String query, HttpServletRequest request) {
        Map<String, SummonerSuggestionDTO> userHistory = getSearchHistoryFromCookie(request);
        List<SummonerSuggestionDTO> list = riotApiService.getSummonerSuggestions(query, userHistory);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(list);
    }

    @GetMapping("/api/me")
    @ResponseBody
    public Callable<ResponseEntity<?>> getMySummoner(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            return () -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noStore())
                    .body(Map.of("error", "Missing or invalid Authorization header. Expected 'Bearer <token>'."));
        }

        String bearerToken = authorizationHeader.substring("Bearer ".length()).trim();
        if (!StringUtils.hasText(bearerToken)) {
            return () -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noStore())
                    .body(Map.of("error", "Empty bearer token."));
        }

        return () -> {
            try {
                Summoner summoner = riotApiService.getSummonerViaRso(bearerToken).join();
                if (summoner == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .cacheControl(CacheControl.noStore())
                            .body(Map.of("error", "Summoner not found or token invalid."));
                }
                return ResponseEntity.ok()
                        .cacheControl(CacheControl.noStore())
                        .body(summoner);
            } catch (Exception e) {
                logger.error("Error in /api/me endpoint: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .cacheControl(CacheControl.noStore())
                        .body(Map.of("error", "Internal error while resolving summoner via RSO."));
            }
        };
    }

    @GetMapping("/api/profile")
    public ResponseEntity<?> getSummonerProfile(@RequestParam("riotId") String riotId,
                                                @RequestParam(value = "includeMatches", defaultValue = "true") boolean includeMatches,
                                                HttpServletRequest request,
                                                HttpServletResponse response,
                                                Locale locale) {
        String normalizedRiotId = riotId != null ? riotId.trim() : null;
        if (!StringUtils.hasText(normalizedRiotId) || !normalizedRiotId.contains("#")) {
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore())
                    .body(Map.of("error", "Invalid Riot ID. Please use the format Name#TAG."));
        }

        String[] parts = normalizedRiotId.split("#", 2);
        String gameName = parts[0].trim();
        String tagLine = parts[1].trim();

        if (!StringUtils.hasText(gameName) || !StringUtils.hasText(tagLine)) {
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore())
                    .body(Map.of("error", "Invalid Riot ID. Name and Tagline cannot be empty."));
        }

        try {
            SummonerProfileData profileData = riotApiService.getSummonerProfileDataAsync(gameName, tagLine).join();
            if (profileData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .cacheControl(CacheControl.noStore())
                        .body(Map.of("error", "Summoner not found."));
            }

            if (profileData.hasError()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noStore())
                        .body(Map.of("error", profileData.errorMessage()));
            }

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("summoner", profileData.summoner());
            payload.put("suggestion", profileData.suggestion());
            payload.put("leagueEntries", profileData.leagueEntries());
            payload.put("championPlayCounts", profileData.championPlayCounts());
            payload.put("profileIconUrl", profileData.profileIconUrl());
            payload.put("riotId", profileData.suggestion() != null ? profileData.suggestion().getRiotId() : normalizedRiotId);
            payload.put("matchesPageSize", matchesPageSize);
            if (includeMatches) {
                payload.put("matchHistory", profileData.matchHistory());
            }

            try {
                payload.put("bases", dataDragonService.getImageBases(null));
            } catch (Exception ex) {
                logger.warn("Failed to load DDragon base URLs: {}", ex.getMessage());
            }
            try {
                payload.put("championSquares", dataDragonService.getChampionKeyToSquareUrl(locale));
            } catch (Exception ex) {
                logger.warn("Failed to load champion square URLs: {}", ex.getMessage());
            }

            if (profileData.suggestion() != null) {
                updateSearchHistoryCookie(request, response, normalizedRiotId, profileData.suggestion());
            }

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .body(payload);
        } catch (CompletionException ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            logger.error("Error processing summoner profile for Riot ID '{}': {}", normalizedRiotId, cause.getMessage(), cause);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore())
                    .body(Map.of("error", "Failed to load summoner profile."));
        } catch (Exception ex) {
            logger.error("Unexpected error during summoner profile for Riot ID '{}': {}", normalizedRiotId, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore())
                    .body(Map.of("error", "Unexpected error while fetching summoner profile."));
        }
    }

    private Map<String, SummonerSuggestionDTO> getSearchHistoryFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request != null ? request.getCookies() : null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SEARCH_HISTORY_COOKIE.equals(cookie.getName())) {
                    try {
                        String decodedValue = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.name());
                        return objectMapper.readValue(decodedValue, new TypeReference<LinkedHashMap<String, SummonerSuggestionDTO>>() {});
                    } catch (IOException | IllegalArgumentException e) {
                        logger.error("Error reading search history cookie: {}", e.getMessage(), e);
                        return new LinkedHashMap<>();
                    }
                }
            }
        }
        return new LinkedHashMap<>();
    }

    private void updateSearchHistoryCookie(HttpServletRequest request, HttpServletResponse response, String riotId, SummonerSuggestionDTO suggestionDTO) {
        String normalizedRiotId = riotId != null ? riotId.trim() : null;
        if (!StringUtils.hasText(normalizedRiotId) || suggestionDTO == null) {
            return;
        }

        Map<String, SummonerSuggestionDTO> history = getSearchHistoryFromCookie(request);

        String normalizedKey = normalizedRiotId.toLowerCase(Locale.ROOT);
        history.remove(normalizedKey);
        history.put(normalizedKey, suggestionDTO);

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
