// Package-Deklaration: definiert den Namespace für die Controller-Klasse
package com.zerox80.riotapi.controller;

// Import für ChampionDetail (Model mit detaillierten Champion-Informationen)
import com.zerox80.riotapi.model.ChampionDetail;
// Import für ChampionSummary (Model mit Champion-Übersichtsdaten)
import com.zerox80.riotapi.model.ChampionSummary;
// Import für DataDragonService (Service für Data Dragon API Zugriff)
import com.zerox80.riotapi.service.DataDragonService;
// Import für CacheControl (definiert Browser-Caching-Verhalten)
import org.springframework.http.CacheControl;
// Import für HttpStatus-Enum (definiert HTTP-Statuscodes wie 200, 404, 500)
import org.springframework.http.HttpStatus;
// Import für ResponseEntity (ermöglicht HTTP-Statuscode und Header-Steuerung)
import org.springframework.http.ResponseEntity;
// Import für GetMapping-Annotation (definiert HTTP GET Endpoints)
import org.springframework.web.bind.annotation.GetMapping;
// Import für PathVariable-Annotation (bindet URL-Pfad-Variablen an Parameter)
import org.springframework.web.bind.annotation.PathVariable;
// Import für RequestMapping-Annotation (definiert Basis-Pfad für Controller)
import org.springframework.web.bind.annotation.RequestMapping;
// Import für RestController-Annotation (kennzeichnet Klasse als REST-Controller)
import org.springframework.web.bind.annotation.RestController;

// Import für SLF4J Logger (Logging-Framework für strukturierte Logs)
import org.slf4j.Logger;
// Import für Logger-Factory (erstellt Logger-Instanzen)
import org.slf4j.LoggerFactory;
// Import für Collections-Utility (bietet statische Methoden für Collections)
import java.util.Collections;
// Import für List-Interface (generische Collection für Listen)
import java.util.List;
// Import für Locale (repräsentiert Sprache/Region für Internationalisierung)
import java.util.Locale;
// Import für Duration (repräsentiert Zeitdauer für Cache-Ablaufzeit)
import java.time.Duration;


// @RestController kennzeichnet diese Klasse als REST-API-Controller
@RestController
// @RequestMapping definiert Basis-Pfad (hier: kein Basis-Pfad, nur Root)
@RequestMapping
public class ChampionsController {

    // Service-Instanz für Data Dragon Zugriff (wird via Dependency Injection bereitgestellt)
    private final DataDragonService dataDragonService;
    // Logger-Instanz für strukturierte Log-Ausgaben dieser Klasse
    private static final Logger log = LoggerFactory.getLogger(ChampionsController.class);


    // Konstruktor mit Dependency Injection für DataDragonService
    public ChampionsController(DataDragonService dataDragonService) { // Service für Data Dragon Daten
        // Zuweisen des DataDragonService zur Instanzvariablen
        this.dataDragonService = dataDragonService;
    }

    // @GetMapping definiert HTTP GET Endpoint unter /api/champions
    @GetMapping("/api/champions")
    // Methode zum Abrufen einer Liste aller Champions mit Basis-Informationen
    public ResponseEntity<List<ChampionSummary>> apiChampions(
            @org.springframework.web.bind.annotation.RequestParam(value = "locale", required = false) String localeParam, // Optionaler Locale-Parameter aus Query-String
            Locale locale) { // Fallback-Locale aus Accept-Language Header
        // Try-Block für Exception-Handling beim Champion-Laden
        try {
            // Abrufen der Champion-Übersichten mit aufgelöstem Locale
            List<ChampionSummary> champions = dataDragonService.getChampionSummaries(resolveLocaleOverride(localeParam, locale));
            // Erstellen von Cache-Control Header (30 Minuten Cache, öffentlich cachebar)
            CacheControl cc = CacheControl.maxAge(Duration.ofMinutes(30)).cachePublic();
            // Rückgabe der Champion-Liste mit HTTP 200 OK und Cache-Headers
            return ResponseEntity.ok().cacheControl(cc).body(champions);
        // Catch-Block für alle Exceptions während des Ladens
        } catch (Exception e) {
            // Loggen einer Warnung (kein Error, da graceful degradation)
            log.warn("/api/champions failed: {}", e.toString());
            // Graceful Degradation: leere Liste zurückgeben statt Fehler (für bessere UX)
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Collections.emptyList());
        }
    }


    // @GetMapping definiert HTTP GET Endpoint unter /api/champions/{id}
    @GetMapping("/api/champions/{id}")
    // Methode zum Abrufen detaillierter Informationen zu einem bestimmten Champion
    public ResponseEntity<ChampionDetail> apiChampion(
            @PathVariable("id") String id, // Champion-ID aus URL-Pfad (z.B. "Ahri")
            @org.springframework.web.bind.annotation.RequestParam(value = "locale", required = false) String localeParam, // Optionaler Locale-Parameter aus Query-String
            Locale locale) { // Fallback-Locale aus Accept-Language Header
        // Try-Block für Exception-Handling beim Champion-Detail-Laden
        try {
            // Abrufen der Champion-Details mit aufgelöstem Locale
            ChampionDetail detail = dataDragonService.getChampionDetail(id, resolveLocaleOverride(localeParam, locale));
            // Validierung: prüfen ob Champion gefunden wurde
            if (detail == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found wenn Champion nicht existiert
            // Erstellen von Cache-Control Header (30 Minuten Cache, öffentlich cachebar)
            CacheControl cc = CacheControl.maxAge(Duration.ofMinutes(30)).cachePublic();
            // Rückgabe der Champion-Details mit HTTP 200 OK und Cache-Headers
            return ResponseEntity.ok().cacheControl(cc).body(detail);
        // Catch-Block für alle Exceptions während des Ladens
        } catch (Exception e) {
            // Rückgabe eines 500 Internal Server Error bei technischen Fehlern
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).cacheControl(CacheControl.noStore()).build();
        }
    }


    // Private Hilfsmethode zum Auflösen des Locale-Parameters mit Fallback
    private Locale resolveLocaleOverride(String localeParam, // Locale-Parameter aus Query-String (optional)
                                        Locale fallback) { // Fallback-Locale wenn Parameter fehlt/ungültig
        // Validierung: prüfen ob Locale-Parameter leer oder nicht vorhanden ist
        if (localeParam == null || localeParam.isBlank()) {
            // Rückgabe des Fallback-Locale bei fehlendem Parameter
            return fallback;
        }
        // Try-Block für Locale-Parsing
        try {
            // Konvertierung des Locale-Strings zu Locale-Objekt (ersetzt _ durch -)
            return Locale.forLanguageTag(localeParam.replace('_', '-'));
        // Catch-Block für ungültige Locale-Strings
        } catch (Exception ignored) {
            // Rückgabe des Fallback-Locale bei Parsing-Fehler
            return fallback;
        }
    }
}
