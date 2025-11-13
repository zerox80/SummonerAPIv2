// Package-Deklaration: definiert den Namespace für die Controller-Klasse
package com.zerox80.riotapi.controller;

// Import für das Summoner-Model (repräsentiert Spielerdaten)
import com.zerox80.riotapi.model.Summoner;
// Import für League-Entry-DTO (Data Transfer Object für Ranglisten-Einträge)
import com.zerox80.riotapi.model.LeagueEntryDTO;
// Import für Match-DTO Version 5 (Spiel-Daten aus der Riot API)
import com.zerox80.riotapi.model.MatchV5Dto;
// Import für Summoner-Vorschlag-DTO (wird für Suchvorschläge verwendet)
import com.zerox80.riotapi.model.SummonerSuggestionDTO;
// Import für den Riot API Service (kommuniziert mit der Riot Games API)
import com.zerox80.riotapi.service.RiotApiService;
// Import für den Data Dragon Service (lädt Champion- und Item-Daten)
import com.zerox80.riotapi.service.DataDragonService;
// Import für Summoner-Profildaten (aggregiert verschiedene Spieldaten)
import com.zerox80.riotapi.model.SummonerProfileData;
// Import für SLF4J Logger (Logging-Framework für strukturierte Logs)
import org.slf4j.Logger;
// Import für Logger-Factory (erstellt Logger-Instanzen)
import org.slf4j.LoggerFactory;
// Import für Dependency Injection via Autowired
import org.springframework.beans.factory.annotation.Autowired;
// Import für Value-Annotation (injiziert Konfigurationswerte aus application.properties)
import org.springframework.beans.factory.annotation.Value;
// Import für GetMapping-Annotation (definiert HTTP GET Endpoints)
import org.springframework.web.bind.annotation.GetMapping;
// Import für RequestParam-Annotation (bindet URL-Parameter an Methoden-Parameter)
import org.springframework.web.bind.annotation.RequestParam;
// Import für StringUtils (hilft bei String-Validierung und -Manipulation)
import org.springframework.util.StringUtils;
// Import für ResponseEntity (ermöglicht HTTP-Statuscode und Header-Steuerung)
import org.springframework.http.ResponseEntity;
// Import für HttpStatus-Enum (definiert HTTP-Statuscodes wie 200, 404, 500)
import org.springframework.http.HttpStatus;
// Import für RequestHeader-Annotation (bindet HTTP-Header an Methoden-Parameter)
import org.springframework.web.bind.annotation.RequestHeader;
// Import für ResponseCookie (erstellt sichere HTTP-Cookies)
import org.springframework.http.ResponseCookie;
// Import für CacheControl (definiert Browser-Caching-Verhalten)
import org.springframework.http.CacheControl;
// Import für ResponseBody-Annotation (konvertiert Rückgabewert zu JSON)
import org.springframework.web.bind.annotation.ResponseBody;
// Import für RestController-Annotation (kennzeichnet Klasse als REST-Controller)
import org.springframework.web.bind.annotation.RestController;

// Import für List-Interface (generische Collection für Listen)
import java.util.List;
// Import für Collections-Utility (bietet statische Methoden für Collections)
import java.util.Collections;
// Import für Locale (repräsentiert Sprache/Region für Internationalisierung)
import java.util.Locale;
// Import für Map-Interface (Schlüssel-Wert-Paare)
import java.util.Map;
// Import für LinkedHashMap (erhält die Einfügereihenfolge von Map-Einträgen)
import java.util.LinkedHashMap;
// Import für Callable (ermöglicht synchrone Operation in asynchronem Context)
import java.util.concurrent.Callable;
// Import für CompletableFuture (ermöglicht asynchrone, nicht-blockierende Operationen)
import java.util.concurrent.CompletableFuture;
// Import für CompletionException (wird bei Fehlern in CompletableFuture geworfen)
import java.util.concurrent.CompletionException;
// Import für Cookie-Klasse (repräsentiert HTTP-Cookies)
import jakarta.servlet.http.Cookie;
// Import für HttpServletRequest (repräsentiert eingehende HTTP-Anfrage)
import jakarta.servlet.http.HttpServletRequest;
// Import für HttpServletResponse (repräsentiert ausgehende HTTP-Antwort)
import jakarta.servlet.http.HttpServletResponse;
// Import für TypeReference (hilft Jackson beim Deserialisieren generischer Typen)
import com.fasterxml.jackson.core.type.TypeReference;
// Import für ObjectMapper (konvertiert zwischen Java-Objekten und JSON)
import com.fasterxml.jackson.databind.ObjectMapper;

// Import für IOException (wird bei I/O-Fehlern geworfen)
import java.io.IOException;
// Import für URLDecoder (dekodiert URL-encodierte Strings)
import java.net.URLDecoder;
// Import für URLEncoder (encodiert Strings für URLs)
import java.net.URLEncoder;
// Import für StandardCharsets (bietet UTF-8 Zeichenkodierung)
import java.nio.charset.StandardCharsets;
// Import für ArrayList (dynamische Array-Implementation)
import java.util.ArrayList;
// Import für Duration (repräsentiert Zeitdauer für Cookie-Ablaufzeit)
import java.time.Duration;


// @RestController kennzeichnet diese Klasse als REST-API-Controller
@RestController
public class SummonerController {

    // Logger-Instanz für strukturierte Log-Ausgaben dieser Klasse
    private static final Logger logger = LoggerFactory.getLogger(SummonerController.class);
    // Service-Instanz für Riot API Kommunikation (wird via Dependency Injection bereitgestellt)
    private final RiotApiService riotApiService;
    // Service-Instanz für Data Dragon Zugriff (Champion/Item Bilder und Daten)
    private final DataDragonService dataDragonService;
    // ObjectMapper für JSON-Serialisierung/Deserialisierung von Cookie-Daten
    private final ObjectMapper objectMapper;
    // Standardanzahl an Matches pro Seite (wird aus application.properties geladen)
    private final int matchesPageSize;
    // Maximale Anzahl an Matches, die pro Request geladen werden dürfen
    private final int maxMatchesPageSize;
    // Maximaler Start-Offset für Match-Paginierung (verhindert zu große Offsets)
    private final int maxMatchesStartOffset;
    // Cookie-Name für die Suchhistorie des Benutzers
    private static final String SEARCH_HISTORY_COOKIE = "searchHistory";
    // Maximale Anzahl an Einträgen in der Suchhistorie
    private static final int MAX_HISTORY_SIZE = 10;


    // @Autowired sorgt für automatische Dependency Injection durch Spring
    @Autowired
    // Konstruktor mit Dependency Injection für alle benötigten Services und Konfigurationswerte
    public SummonerController(RiotApiService riotApiService, // Service für Riot API Zugriff
                              DataDragonService dataDragonService, // Service für Champion/Item Daten
                              ObjectMapper objectMapper, // JSON-Mapper für Cookie-Serialisierung
                              @Value("${ui.matches.page-size:10}") int matchesPageSize, // Seitengröße aus Config (Standard: 10)
                              @Value("${ui.matches.max-page-size:40}") int maxMatchesPageSize, // Max. Seitengröße (Standard: 40)
                              @Value("${ui.matches.max-start-offset:1000}") int maxMatchesStartOffset) { // Max. Offset (Standard: 1000)
        // Zuweisen des RiotApiService zur Instanzvariablen
        this.riotApiService = riotApiService;
        // Zuweisen des DataDragonService zur Instanzvariablen
        this.dataDragonService = dataDragonService;
        // Zuweisen des ObjectMapper zur Instanzvariablen
        this.objectMapper = objectMapper;
        // Setzen der Match-Seitengröße mit Validierung (mindestens 1, sonst 10)
        this.matchesPageSize = matchesPageSize > 0 ? matchesPageSize : 10;
        // Berechnen des größeren Werts zwischen matchesPageSize und maxMatchesPageSize
        int pageLimit = Math.max(this.matchesPageSize, maxMatchesPageSize);
        // Setzen der maximalen Seitengröße mit Validierung (mindestens matchesPageSize)
        this.maxMatchesPageSize = pageLimit > 0 ? pageLimit : this.matchesPageSize;
        // Setzen des maximalen Start-Offsets (mindestens 0)
        this.maxMatchesStartOffset = Math.max(0, maxMatchesStartOffset);
    }


    // @GetMapping definiert HTTP GET Endpoint unter /api/matches
    @GetMapping("/api/matches")
    // @ResponseBody sorgt für automatische JSON-Serialisierung des Rückgabewerts
    @ResponseBody
    // Methode zum Laden weiterer Matches mit Paginierung (asynchron via CompletableFuture)
    public CompletableFuture<ResponseEntity<?>> getMoreMatches(@RequestParam("riotId") String riotId, // Riot ID im Format Name#TAG (pflichtfeld)
                                                               @RequestParam(value = "start", defaultValue = "0") int start, // Start-Index für Paginierung (Standard: 0)
                                                               @RequestParam(value = "count", defaultValue = "10") int count) { // Anzahl der zu ladenden Matches (Standard: 10)
        // Trimmen der Riot ID und entfernen führender/nachfolgender Leerzeichen
        String trimmedRiotId = riotId != null ? riotId.trim() : null;
        // Validierung: prüfen ob Riot ID vorhanden und im korrekten Format (muss # enthalten)
        if (!StringUtils.hasText(trimmedRiotId) || !trimmedRiotId.contains("#")) {
            // Rückgabe eines 400 Bad Request mit Fehlermeldung (keine Cache-Speicherung)
            return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // Verhindert Browser-Caching der Fehlerantwort
                    .body(Map.of("error", "Invalid riotId format. Expected Name#TAG"))); // JSON-Fehlermeldung
        }

        // Aufteilen der Riot ID am #-Zeichen (maximal 2 Teile)
        String[] parts = trimmedRiotId.split("#", 2);
        // Extrahieren des Spielernamens (Teil vor dem #)
        String gameName = parts[0].trim();
        // Extrahieren des Taglines (Teil nach dem #)
        String tagLine = parts[1].trim();
        // Validierung: prüfen ob Name und Tagline nicht leer sind
        if (!StringUtils.hasText(gameName) || !StringUtils.hasText(tagLine)) {
            // Rückgabe eines 400 Bad Request bei leeren Name/Tagline
            return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                    .body(Map.of("error", "Invalid riotId format. Expected Name#TAG"))); // Fehlermeldung
        }
        // Validierung: count muss positiv sein
        if (count <= 0) {
            // Rückgabe eines 400 Bad Request bei negativem oder null count
            return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                    .body(Map.of("error", "Parameter 'count' must be positive."))); // Fehlermeldung
        }
        // Validierung: count darf maxMatchesPageSize nicht überschreiten
        if (count > maxMatchesPageSize) {
            // Rückgabe eines 400 Bad Request wenn zu viele Matches angefordert werden
            return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                    .body(Map.of("error", "Maximum matches per request is " + maxMatchesPageSize + "."))); // Fehlermeldung mit Limit
        }

        // Sanitierung des Start-Parameters (mindestens 0, keine negativen Werte)
        final int sanitizedStart = Math.max(0, start);
        // Validierung: start darf maxMatchesStartOffset nicht überschreiten (wenn konfiguriert)
        if (maxMatchesStartOffset > 0 && sanitizedStart > maxMatchesStartOffset) {
            // Rückgabe eines 400 Bad Request bei zu großem Offset
            return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                    .body(Map.of("error", "Parameter 'start' exceeds allowed offset of " + maxMatchesStartOffset + "."))); // Fehlermeldung
        }

        // Asynchroner Aufruf: Summoner-Daten per Riot ID abrufen
        return riotApiService.getSummonerByRiotId(gameName, tagLine)
                // Nach erfolgreichem Laden des Summoners: Match-Historie laden
                .thenCompose(summoner -> {
                    // Validierung: prüfen ob Summoner gefunden wurde und PUUID vorhanden ist
                    if (summoner == null || !StringUtils.hasText(summoner.getPuuid())) {
                        // Rückgabe eines 404 Not Found wenn Summoner nicht existiert
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                                .body(Map.of("error", "Summoner not found."))); // Fehlermeldung
                    }
                    // Asynchroner Aufruf: Match-Historie mit Paginierung laden
                    return riotApiService.getMatchHistoryPaged(summoner.getPuuid(), sanitizedStart, count)
                            // Nach erfolgreichem Laden: Response mit Match-Liste erstellen
                            .thenApply(list -> ResponseEntity.ok() // HTTP 200 OK Status
                                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching (Live-Daten)
                                    .body(list != null ? list : Collections.emptyList())); // Liste zurückgeben (leere Liste falls null)
                })
                // Exception-Handling für alle Fehler in der asynchronen Kette
                .exceptionally(ex -> {
                    // Extrahieren der ursprünglichen Exception aus CompletionException
                    Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;
                    // Loggen des Fehlers mit vollständiger Stack-Trace
                    logger.error("/api/matches error: {}", cause.getMessage(), cause);
                    // Rückgabe eines 500 Internal Server Error bei technischen Fehlern
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                            .body(Map.of("error", "Failed to load matches.")); // Generische Fehlermeldung
                });
    }


    // @GetMapping definiert HTTP GET Endpoint unter /api/summoner-suggestions
    @GetMapping("/api/summoner-suggestions")
    // Methode für Summoner-Suchvorschläge basierend auf Suchhistorie und Query
    public ResponseEntity<List<SummonerSuggestionDTO>> summonerSuggestions(@RequestParam("query") String query, // Suchanfrage des Benutzers
                                                                             HttpServletRequest request) { // HTTP-Request für Cookie-Zugriff
        // Laden der Suchhistorie aus dem Browser-Cookie des Benutzers
        Map<String, SummonerSuggestionDTO> userHistory = getSearchHistoryFromCookie(request);
        // Abrufen der Suchvorschläge aus dem Service (kombiniert History + neue Suche)
        List<SummonerSuggestionDTO> list = riotApiService.getSummonerSuggestions(query, userHistory);
        // Rückgabe der Vorschlagsliste mit HTTP 200 OK Status
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore()) // Keine Browser-Caching (Live-Daten)
                .body(list); // Liste der Vorschläge als JSON-Response
    }


    // @GetMapping definiert HTTP GET Endpoint unter /api/me (für OAuth-Authentifizierung)
    @GetMapping("/api/me")
    // @ResponseBody sorgt für automatische JSON-Serialisierung des Rückgabewerts
    @ResponseBody
    // Methode zum Abrufen des eigenen Summoner-Profils via RSO (Riot Sign-On) Token
    public Callable<ResponseEntity<?>> getMySummoner(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) { // Authorization Header mit Bearer Token (optional)
        // Validierung: prüfen ob Authorization Header vorhanden und mit "Bearer " beginnt
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            // Rückgabe einer Lambda-Funktion (Callable) mit 401 Unauthorized
            return () -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                    .body(Map.of("error", "Missing or invalid Authorization header. Expected 'Bearer <token>'.")); // Fehlermeldung
        }

        // Extrahieren des Bearer Tokens aus dem Authorization Header
        String bearerToken = authorizationHeader.substring("Bearer ".length()).trim();
        // Validierung: prüfen ob Token nicht leer ist nach dem Trimmen
        if (!StringUtils.hasText(bearerToken)) {
            // Rückgabe einer Lambda-Funktion (Callable) mit 401 Unauthorized bei leerem Token
            return () -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                    .body(Map.of("error", "Empty bearer token.")); // Fehlermeldung
        }

        // Rückgabe einer Lambda-Funktion (Callable) für asynchrone Verarbeitung
        return () -> {
            // Try-Block für Exception-Handling
            try {
                // Asynchroner Aufruf: Summoner via RSO Token abrufen (join() blockiert bis fertig)
                Summoner summoner = riotApiService.getSummonerViaRso(bearerToken).join();
                // Validierung: prüfen ob Summoner gefunden wurde
                if (summoner == null) {
                    // Rückgabe eines 404 Not Found wenn Token ungültig oder Summoner nicht existiert
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                            .body(Map.of("error", "Summoner not found or token invalid.")); // Fehlermeldung
                }
                // Rückgabe des Summoner-Objekts mit HTTP 200 OK Status
                return ResponseEntity.ok()
                        .cacheControl(CacheControl.noStore()) // Keine Browser-Caching (sensible Daten)
                        .body(summoner); // Summoner-Daten als JSON-Response
            // Catch-Block für alle Exceptions während der Verarbeitung
            } catch (Exception e) {
                // Loggen des Fehlers mit vollständiger Stack-Trace
                logger.error("Error in /api/me endpoint: {}", e.getMessage(), e);
                // Rückgabe eines 500 Internal Server Error bei technischen Fehlern
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                        .body(Map.of("error", "Internal error while resolving summoner via RSO.")); // Generische Fehlermeldung
            }
        };
    }


    // @GetMapping definiert HTTP GET Endpoint unter /api/profile (Hauptendpunkt für Spielerprofile)
    @GetMapping("/api/profile")
    // Methode zum Abrufen eines vollständigen Summoner-Profils mit allen Details
    public ResponseEntity<?> getSummonerProfile(@RequestParam("riotId") String riotId, // Riot ID im Format Name#TAG (pflichtfeld)
                                                @RequestParam(value = "includeMatches", defaultValue = "true") boolean includeMatches, // Flag ob Match-Historie inkludiert werden soll (Standard: true)
                                                HttpServletRequest request, // HTTP-Request für Cookie-Zugriff
                                                HttpServletResponse response, // HTTP-Response zum Setzen von Cookies
                                                Locale locale) { // Sprach-/Region-Einstellung des Benutzers
        // Normalisieren der Riot ID durch Trimmen führender/nachfolgender Leerzeichen
        String normalizedRiotId = riotId != null ? riotId.trim() : null;
        // Validierung: prüfen ob Riot ID vorhanden und im korrekten Format (muss # enthalten)
        if (!StringUtils.hasText(normalizedRiotId) || !normalizedRiotId.contains("#")) {
            // Rückgabe eines 400 Bad Request bei ungültigem Format
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                    .body(Map.of("error", "Invalid Riot ID. Please use the format Name#TAG.")); // Fehlermeldung
        }

        // Aufteilen der Riot ID am #-Zeichen (maximal 2 Teile)
        String[] parts = normalizedRiotId.split("#", 2);
        // Extrahieren des Spielernamens (Teil vor dem #)
        String gameName = parts[0].trim();
        // Extrahieren des Taglines (Teil nach dem #)
        String tagLine = parts[1].trim();

        // Validierung: prüfen ob Name und Tagline nicht leer sind
        if (!StringUtils.hasText(gameName) || !StringUtils.hasText(tagLine)) {
            // Rückgabe eines 400 Bad Request bei leeren Name/Tagline
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                    .body(Map.of("error", "Invalid Riot ID. Name and Tagline cannot be empty.")); // Fehlermeldung
        }

        // Try-Block für Exception-Handling bei Profildaten-Abruf
        try {
            // Asynchroner Aufruf: vollständige Profildaten abrufen (join() blockiert bis fertig)
            SummonerProfileData profileData = riotApiService.getSummonerProfileDataAsync(gameName, tagLine).join();
            // Validierung: prüfen ob Profildaten gefunden wurden
            if (profileData == null) {
                // Rückgabe eines 404 Not Found wenn Summoner nicht existiert
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                        .body(Map.of("error", "Summoner not found.")); // Fehlermeldung
            }

            // Validierung: prüfen ob Profildaten einen Fehler enthalten (z.B. Riot API Fehler)
            if (profileData.hasError()) {
                // Rückgabe eines 200 OK mit Fehlermeldung (kein Server-Fehler, sondern Daten-Fehler)
                return ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                        .body(Map.of("error", profileData.errorMessage())); // Fehlertext aus Profildaten
            }

            // Erstellen einer LinkedHashMap für die Response-Payload (erhält Reihenfolge)
            Map<String, Object> payload = new LinkedHashMap<>();
            // Hinzufügen des Summoner-Objekts zur Payload
            payload.put("summoner", profileData.summoner());
            // Hinzufügen des Suggestion-Objekts (für Autocomplete/Suchhistorie)
            payload.put("suggestion", profileData.suggestion());
            // Hinzufügen der Ranglisten-Einträge (Solo/Duo, Flex, etc.)
            payload.put("leagueEntries", profileData.leagueEntries());
            // Hinzufügen der Champion-Spielstatistiken (wie oft welcher Champion gespielt)
            payload.put("championPlayCounts", profileData.championPlayCounts());
            // Hinzufügen der Profile-Icon URL (Profilbild des Spielers)
            payload.put("profileIconUrl", profileData.profileIconUrl());
            // Hinzufügen der Riot ID (aus Suggestion falls vorhanden, sonst normalisierte ID)
            payload.put("riotId", profileData.suggestion() != null ? profileData.suggestion().getRiotId() : normalizedRiotId);
            // Hinzufügen der konfigurierten Match-Seitengröße
            payload.put("matchesPageSize", matchesPageSize);
            // Bedingtes Hinzufügen der Match-Historie (nur wenn Parameter includeMatches = true)
            if (includeMatches) {
                // Hinzufügen der Match-Historie zur Payload
                payload.put("matchHistory", profileData.matchHistory());
            }

            // Try-Block für optionales Laden der Data Dragon Base URLs
            try {
                // Abrufen der Image-Base-URLs vom Data Dragon Service (für Champion/Item Bilder)
                Map<String, String> bases = dataDragonService.getImageBases(null);
                // Validierung: nur hinzufügen wenn Base-URLs vorhanden sind
                if (bases != null && !bases.isEmpty()) {
                    // Hinzufügen der Base-URLs zur Payload
                    payload.put("bases", bases);
                }
            // Catch-Block für Fehler beim Laden der Base-URLs (nicht kritisch, daher nur Warning)
            } catch (Exception ex) {
                // Loggen einer Warnung (kein Fehler, da Daten optional sind)
                logger.warn("Failed to load DDragon base URLs: {}", ex.getMessage());
            }
            // Try-Block für optionales Laden der Champion-Square-URLs
            try {
                // Abrufen der Champion-Square-URLs (Champion-Bilder nach Key-ID zugeordnet)
                Map<Integer, String> championSquares = dataDragonService.getChampionKeyToSquareUrl(locale);
                // Validierung: nur hinzufügen wenn Champion-URLs vorhanden sind
                if (championSquares != null && !championSquares.isEmpty()) {
                    // Hinzufügen der Champion-Square-URLs zur Payload
                    payload.put("championSquares", championSquares);
                }
            // Catch-Block für Fehler beim Laden der Champion-URLs (nicht kritisch, daher nur Warning)
            } catch (Exception ex) {
                // Loggen einer Warnung (kein Fehler, da Daten optional sind)
                logger.warn("Failed to load champion square URLs: {}", ex.getMessage());
            }

            // Bedingtes Aktualisieren der Suchhistorie-Cookie (nur wenn Suggestion vorhanden)
            if (profileData.suggestion() != null) {
                // Aktualisieren des Search-History-Cookies mit der aktuellen Suche
                updateSearchHistoryCookie(request, response, normalizedRiotId, profileData.suggestion());
            }

            // Rückgabe der vollständigen Payload mit HTTP 200 OK Status
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching (Live-Daten)
                    .body(payload); // Payload mit allen Profildaten als JSON-Response
        // Catch-Block für CompletionException (asynchrone Fehler)
        } catch (CompletionException ex) {
            // Extrahieren der ursprünglichen Exception aus CompletionException
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            // Loggen des Fehlers mit Riot ID und vollständiger Stack-Trace
            logger.error("Error processing summoner profile for Riot ID '{}': {}", normalizedRiotId, cause.getMessage(), cause);
            // Rückgabe eines 500 Internal Server Error bei asynchronen Fehlern
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                    .body(Map.of("error", "Failed to load summoner profile.")); // Generische Fehlermeldung
        // Catch-Block für alle anderen unerwarteten Exceptions
        } catch (Exception ex) {
            // Loggen des Fehlers mit Riot ID und vollständiger Stack-Trace
            logger.error("Unexpected error during summoner profile for Riot ID '{}': {}", normalizedRiotId, ex.getMessage(), ex);
            // Rückgabe eines 500 Internal Server Error bei unerwarteten Fehlern
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore()) // Keine Browser-Caching
                    .body(Map.of("error", "Unexpected error while fetching summoner profile.")); // Generische Fehlermeldung
        }
    }


    // Private Hilfsmethode zum Laden der Suchhistorie aus dem Browser-Cookie
    private Map<String, SummonerSuggestionDTO> getSearchHistoryFromCookie(HttpServletRequest request) {
        // Abrufen aller Cookies aus dem Request (null-safe)
        Cookie[] cookies = request != null ? request.getCookies() : null;

        // Validierung: prüfen ob Cookies vorhanden sind
        if (cookies != null) {
            // Iterieren über alle Cookies im Request
            for (Cookie cookie : cookies) {
                // Prüfen ob aktueller Cookie der Suchhistorie-Cookie ist
                if (SEARCH_HISTORY_COOKIE.equals(cookie.getName())) {
                    // Try-Block für JSON-Deserialisierung
                    try {
                        // URL-Dekodierung des Cookie-Werts (UTF-8 Encoding)
                        String decodedValue = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.name());
                        // Deserialisieren des JSON-Strings zurück zu einer Map (mit TypeReference für Generics)
                        return objectMapper.readValue(decodedValue, new TypeReference<LinkedHashMap<String, SummonerSuggestionDTO>>() {});
                    // Catch-Block für I/O- und Parsing-Fehler
                    } catch (IOException | IllegalArgumentException e) {
                        // Loggen des Fehlers (Cookie könnte korrupt sein)
                        logger.error("Error reading search history cookie: {}", e.getMessage(), e);
                        // Rückgabe einer leeren Map bei Fehler
                        return new LinkedHashMap<>();
                    }
                }
            }
        }
        // Rückgabe einer leeren Map wenn kein Cookie gefunden wurde
        return new LinkedHashMap<>();
    }


    // Private Hilfsmethode zum Aktualisieren der Suchhistorie im Browser-Cookie
    private void updateSearchHistoryCookie(HttpServletRequest request, // HTTP-Request für Cookie-Zugriff
                                           HttpServletResponse response, // HTTP-Response zum Setzen des Cookies
                                           String riotId, // Riot ID des gesuchten Spielers
                                           SummonerSuggestionDTO suggestionDTO) { // Suggestion-Objekt mit Profildaten
        // Normalisieren der Riot ID durch Trimmen führender/nachfolgender Leerzeichen
        String normalizedRiotId = riotId != null ? riotId.trim() : null;
        // Validierung: prüfen ob Riot ID und Suggestion-Objekt vorhanden sind
        if (!StringUtils.hasText(normalizedRiotId) || suggestionDTO == null) {
            // Frühes Abbrechen wenn Daten ungültig sind
            return;
        }

        // Laden der bestehenden Suchhistorie aus dem Cookie
        Map<String, SummonerSuggestionDTO> history = getSearchHistoryFromCookie(request);

        // Normalisieren des Schlüssels zu Kleinbuchstaben (case-insensitive)
        String normalizedKey = normalizedRiotId.toLowerCase(Locale.ROOT);
        // Entfernen des alten Eintrags (falls vorhanden) um Duplikate zu vermeiden
        history.remove(normalizedKey);
        // Hinzufügen des neuen Eintrags am Ende der Map (neueste zuletzt)
        history.put(normalizedKey, suggestionDTO);

        // Begrenzung der History-Größe auf MAX_HISTORY_SIZE (FIFO-Prinzip)
        while (history.size() > MAX_HISTORY_SIZE) {
            // Abrufen des ältesten Schlüssels (erster Eintrag in LinkedHashMap)
            String oldestKey = history.keySet().iterator().next();
            // Entfernen des ältesten Eintrags
            history.remove(oldestKey);
        }

        // Try-Block für JSON-Serialisierung und Cookie-Erstellung
        try {
            // Serialisieren der History-Map zu JSON-String
            String jsonHistory = objectMapper.writeValueAsString(history);
            // URL-Kodierung des JSON-Strings (UTF-8 Encoding für Cookie-Kompatibilität)
            String encodedValue = URLEncoder.encode(jsonHistory, StandardCharsets.UTF_8.name());
            // Prüfen ob Request über HTTPS kam (für Secure-Cookie-Flag)
            boolean secure = request.isSecure();
            // Falls nicht secure, prüfen ob hinter einem Proxy (X-Forwarded-Proto Header)
            if (!secure) {
                // Abrufen des X-Forwarded-Proto Headers (gesetzt von Reverse Proxies)
                String forwardedProto = request.getHeader("X-Forwarded-Proto");
                // Validierung: prüfen ob Header vorhanden ist
                if (StringUtils.hasText(forwardedProto)) {
                    // Setzen von secure = true wenn Proxy HTTPS verwendet
                    secure = forwardedProto.trim().equalsIgnoreCase("https");
                }
            }

            // Erstellen eines ResponseCookie mit allen Sicherheitsflags
            ResponseCookie cookie = ResponseCookie.from(SEARCH_HISTORY_COOKIE, encodedValue) // Cookie-Name und Wert
                    .httpOnly(true) // Verhindert JavaScript-Zugriff (XSS-Schutz)
                    .secure(secure) // Nur über HTTPS senden (wenn HTTPS aktiv)
                    .path("/") // Cookie für gesamte Domain gültig
                    .sameSite("Lax") // CSRF-Schutz (Cookie nur bei Navigation senden)
                    .maxAge(Duration.ofDays(30)) // Cookie-Ablaufzeit: 30 Tage
                    .build(); // Cookie-Objekt erstellen
            // Hinzufügen des Set-Cookie Headers zur Response
            response.addHeader("Set-Cookie", cookie.toString());
        // Catch-Block für Serialisierungs- und Encoding-Fehler
        } catch (IOException e) {
            // Loggen des Fehlers (Cookie-Update ist nicht kritisch)
            logger.error("Error writing search history cookie: " + e.getMessage(), e);
        }
    }
// Klassenende
}
