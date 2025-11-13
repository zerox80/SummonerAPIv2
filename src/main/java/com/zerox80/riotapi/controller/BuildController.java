// Package-Deklaration: definiert den Namespace für die Controller-Klasse
package com.zerox80.riotapi.controller;

// Import für ChampionBuildDto (Data Transfer Object für Champion-Build-Daten)
import com.zerox80.riotapi.dto.ChampionBuildDto;
// Import für BuildAggregationService (Service zur Aggregation von Build-Daten)
import com.zerox80.riotapi.service.BuildAggregationService;
// Import für Value-Annotation (injiziert Konfigurationswerte aus application.properties)
import org.springframework.beans.factory.annotation.Value;
// Import für HttpStatus-Enum (definiert HTTP-Statuscodes wie 200, 403, 404)
import org.springframework.http.HttpStatus;
// Import für ResponseEntity (ermöglicht HTTP-Statuscode und Header-Steuerung)
import org.springframework.http.ResponseEntity;
// Import für StringUtils (hilft bei String-Validierung und -Manipulation)
import org.springframework.util.StringUtils;
// Import für verschiedene Web-Annotationen (REST-Controller und Mappings)
import org.springframework.web.bind.annotation.*;

// Import für HttpServletRequest (repräsentiert eingehende HTTP-Anfrage)
import jakarta.servlet.http.HttpServletRequest;

// Import für Locale (repräsentiert Sprache/Region für Internationalisierung)
import java.util.Locale;


// @RestController kennzeichnet diese Klasse als REST-API-Controller
@RestController
// @RequestMapping definiert Basis-Pfad für alle Endpoints in dieser Klasse
@RequestMapping("/api")
public class BuildController {


    // Service-Instanz für Build-Aggregation (wird via Dependency Injection bereitgestellt)
    private final BuildAggregationService agg;


    // Konfigurationswert: aktiviert oder deaktiviert den Aggregation-Trigger-Endpoint
    @Value("${build.agg.trigger-enabled:false}") // Standard: false (deaktiviert)
    private boolean triggerEnabled;


    // Konfigurationswert: geheimer Token für Zugriff auf den Aggregation-Endpoint
    @Value("${build.agg.trigger-token:}") // Standard: leer (kein Token konfiguriert)
    private String triggerToken;


    // Konstruktor mit Dependency Injection für BuildAggregationService
    public BuildController(BuildAggregationService agg) { // Service für Build-Aggregation
        // Zuweisen des BuildAggregationService zur Instanzvariablen
        this.agg = agg;
    }


    // @GetMapping definiert HTTP GET Endpoint unter /api/champions/{id}/build
    @GetMapping("/champions/{id}/build")
    // Methode zum Abrufen von Champion-Build-Daten für einen bestimmten Champion
    public ResponseEntity<ChampionBuildDto> getBuild(@PathVariable("id") String id, // Champion-ID aus URL-Pfad (z.B. "Ahri")
                                                     @RequestParam(value = "queueId", required = false) Integer queueId, // Queue-ID (optional, z.B. 420 für Solo/Duo)
                                                     @RequestParam(value = "role", required = false) String role, // Rolle (optional, z.B. "MIDDLE")
                                                     Locale locale) { // Sprach-/Region-Einstellung des Benutzers
        // Laden des Champion-Builds aus dem Service (filtert nach Champion, Queue und Rolle)
        ChampionBuildDto dto = agg.loadBuild(id, queueId, role, locale);
        // Rückgabe des Build-DTOs mit HTTP 200 OK Status
        return ResponseEntity.ok(dto); // Build-Daten als JSON-Response
    }


    // @PostMapping definiert HTTP POST Endpoint unter /api/champions/{id}/aggregate
    @PostMapping("/champions/{id}/aggregate")
    // Methode zum Starten einer Build-Aggregation für einen bestimmten Champion (Admin-Funktion)
    public ResponseEntity<String> aggregate(@PathVariable("id") String id, // Champion-ID aus URL-Pfad (z.B. "Ahri")
                                            @RequestParam(value = "queueId", required = false) Integer queueId, // Queue-ID (optional, z.B. 420 für Solo/Duo)
                                            @RequestParam(value = "pages", defaultValue = "1") int pages, // Anzahl der zu durchsuchenden Summoner-Seiten (Standard: 1)
                                            @RequestParam(value = "matchesPerSummoner", defaultValue = "8") int matchesPerSummoner, // Matches pro Summoner (Standard: 8)
                                            @RequestParam(value = "maxSummoners", defaultValue = "75") int maxSummoners, // Maximale Anzahl Summoner (Standard: 75)
                                            Locale locale, // Sprach-/Region-Einstellung des Benutzers
                                            HttpServletRequest request) { // HTTP-Request für Header-Zugriff
        // Validierung: prüfen ob Aggregation-Trigger aktiviert ist
        if (!triggerEnabled) {
            // Rückgabe eines 403 Forbidden wenn Feature deaktiviert ist
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Aggregation trigger is disabled");
        }
        // Validierung: prüfen ob Token konfiguriert ist
        if (!StringUtils.hasText(triggerToken)) {
            // Rückgabe eines 403 Forbidden wenn kein Token konfiguriert wurde
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Aggregation trigger token is not configured");
        }

        // Abrufen des X-Aggregation-Token Headers aus dem Request
        String providedToken = request.getHeader("X-Aggregation-Token");
        // Validierung: prüfen ob bereitgestellter Token mit konfiguriertem Token übereinstimmt
        if (!triggerToken.equals(providedToken)) {
            // Rückgabe eines 403 Forbidden bei ungültigem Token
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid aggregation trigger token");
        }
        // Validierung: prüfen ob Queue-ID unterstützt wird (nur 420 und 440 erlaubt)
        if (queueId != null && queueId != 420 && queueId != 440) {
            // Rückgabe eines 400 Bad Request bei nicht unterstützter Queue
            return ResponseEntity.badRequest().body("Unsupported queueId. Allowed values are 420 (Solo/Duo) or 440 (Flex).");
        }
        // Setzen der effektiven Queue-ID (Standard: 420 wenn nicht angegeben)
        Integer effectiveQueueId = (queueId != null) ? queueId : 420;
        // Starten der asynchronen Aggregation für den Champion (läuft im Hintergrund)
        agg.aggregateChampion(id, effectiveQueueId, pages, matchesPerSummoner, maxSummoners, locale);
        // Rückgabe eines 202 Accepted Status (Anfrage akzeptiert, Verarbeitung läuft)
        return ResponseEntity.accepted().body("Aggregation started for " + id); // Bestätigungsmeldung
    }
}
