package com.zerox80.riotapi.service; // Paketdeklaration für den Service-Layer

import org.slf4j.Logger; // Import für Logging-Schnittstelle
import org.slf4j.LoggerFactory; // Import für Logger-Factory zur Logger-Instanziierung
import org.springframework.beans.factory.annotation.Value; // Import für Property-Injection aus application.properties
import org.springframework.scheduling.annotation.Scheduled; // Import für geplante Task-Ausführung (Cron-Jobs)
import org.springframework.stereotype.Service; // Import für Service-Komponenten-Annotation

import java.util.Arrays; // Import für Array-Utility-Methoden
import java.util.List; // Import für Listen-Collections
import java.util.Locale; // Import für Sprach-/Regions-Einstellungen


@Service // Markiert diese Klasse als Spring Service-Komponente für Dependency Injection
public class BuildAggregationScheduler { // Scheduler-Service für automatisierte nächtliche Build-Aggregation

    private static final Logger log = LoggerFactory.getLogger(BuildAggregationScheduler.class); // Logger-Instanz für Logging in diesem Service

    private final BuildAggregationService agg; // Service für Build-Aggregations-Business-Logik

    @Value("${build.agg.enabled:false}") // Property-Injection: Liest Konfiguration aus application.properties (Default: false)
    private boolean enabled; // Flag ob Build-Aggregation aktiviert ist

    @Value("${build.agg.queue-id:420}") // Property-Injection: Queue-ID für Aggregation (Default: 420 = SoloQ)
    private int queueId; // Riot Queue-ID (420=SoloQ, 440=FlexQ)

    @Value("${build.agg.pages:1}") // Property-Injection: Anzahl Seiten von Spieler-Listen pro Tier/Division (Default: 1)
    private int pages; // Anzahl der zu scannenden Seiten pro Liga-Tier

    @Value("${build.agg.matches-per-summoner:6}") // Property-Injection: Matches pro Summoner (Default: 6)
    private int matchesPerSummoner; // Anzahl der zu analysierenden Matches pro Spieler

    @Value("${build.agg.max-summoners:50}") // Property-Injection: Maximale Anzahl Summoner (Default: 50)
    private int maxSummoners; // Maximale Anzahl Spieler für Datensammlung (Performance-Limit)

    @Value("${build.agg.champions:}") // Property-Injection: Komma-separierte Liste von Champion-IDs (Default: leer)
    private String championsCsv; // CSV-String mit Champion-IDs zur Aggregation (z.B. "Anivia,Ashe,Zed")


    public BuildAggregationScheduler(BuildAggregationService agg) { // Konstruktor mit Dependency Injection
        this.agg = agg; // Weist den injizierten Service der Instanzvariable zu
    }


    @Scheduled(cron = "${build.agg.cron:0 15 3 * * *}") // Scheduled-Annotation: Führt Methode nach Cron-Ausdruck aus (Default: täglich um 3:15 Uhr)
    public void runNightly() { // Nächtliche Scheduled-Task für Build-Aggregation
        if (!enabled) { // Prüft ob Aggregation aktiviert ist
            return; // Beendet Methode vorzeitig wenn deaktiviert (Silent Skip)
        }
        List<String> champs = parseChampions(championsCsv); // Parst CSV-String zu Champion-Liste
        if (champs.isEmpty()) { // Validierung: Prüft ob Champions konfiguriert sind
            log.warn("Build aggregation enabled but no champions configured (build.agg.champions). Skipping."); // Warnung wenn keine Champions konfiguriert
            return; // Beendet Methode vorzeitig
        }
        Locale locale = Locale.US; // server-side aggregation locale for static lookups
        for (String champ : champs) { // Iteriert über alle konfigurierten Champions
            try { // Try-Block für fehlertolerante Verarbeitung (ein Fehler stoppt nicht alle)
                agg.aggregateChampion(champ, queueId, pages, matchesPerSummoner, maxSummoners, locale); // Asynchroner Aufruf: Startet Build-Aggregation für einen Champion
            } catch (Exception e) { // Fängt Fehler für einzelnen Champion ab
                log.warn("Aggregation for {} failed: {}", champ, e.toString()); // Warnung bei Aggregationsfehler (nicht kritisch)
            }
        }
    }

    private List<String> parseChampions(String csv) { // Private Hilfsmethode zum Parsen der Champion-CSV
        if (csv == null || csv.isBlank()) return List.of(); // Gibt leere Liste zurück wenn CSV leer
        return Arrays.stream(csv.split(",")) // Stream über CSV-Teile (Split bei Komma)
                .map(String::trim) // Entfernt Leerzeichen um jedes Element
                .filter(s -> !s.isEmpty()) // Filtert leere Strings aus
                .toList(); // Sammelt in unveränderliche Liste (Java 16+)
    }
} // Ende der BuildAggregationScheduler-Klasse
