package com.zerox80.riotapi.service; // Paketdeklaration für den Service-Layer

import com.zerox80.riotapi.model.LeagueEntryDTO; // Import für Liga-Einträge-DTO
import com.zerox80.riotapi.model.MatchV5Dto; // Import für Match-Daten-DTO
import com.zerox80.riotapi.model.PlayerLpRecord; // Import für LP-Record-Entity
import com.zerox80.riotapi.model.Summoner; // Import für Summoner-DTO
import com.zerox80.riotapi.repository.PlayerLpRecordRepository; // Import für Database-Repository zum Speichern von LP-Records
import org.slf4j.Logger; // Import für Logging-Schnittstelle
import org.slf4j.LoggerFactory; // Import für Logger-Factory zur Logger-Instanziierung
import org.springframework.beans.factory.annotation.Autowired; // Import für Dependency Injection
import org.springframework.stereotype.Service; // Import für Service-Komponenten-Annotation
import org.springframework.transaction.annotation.Transactional; // Import für Transaktionsverwaltung bei Database-Operationen
import org.springframework.util.StringUtils; // Import für String-Utility-Funktionen

import java.time.Duration; // Import für Zeitdauer-Berechnungen
import java.time.Instant; // Import für Zeitstempel-Verwaltung
import java.util.ArrayList; // Import für ArrayList-Implementierung
import java.util.Collections; // Import für Collection-Utility-Methoden
import java.util.HashMap; // Import für HashMap-Implementierung
import java.util.HashSet; // Import für HashSet-Implementierung
import java.util.List; // Import für Listen-Collections
import java.util.Map; // Import für Map-Interface
import java.util.Objects; // Import für Object-Utility-Methoden (z.B. equals)
import java.util.Set; // Import für Set-Interface


@Service // Markiert diese Klasse als Spring Service-Komponente für Dependency Injection
public class PlayerLpRecordService { // Service-Klasse für LP (League Points) Record-Verwaltung und LP-Änderungs-Berechnungen

    private static final Logger logger = LoggerFactory.getLogger(PlayerLpRecordService.class); // Logger-Instanz für Logging in diesem Service
    private static final Duration MATCH_END_TOLERANCE = Duration.ofMinutes(10); // Konstante: Toleranz von 10 Minuten für LP-Record-Matching (falls Zeitstempel leicht abweichen)
    private final PlayerLpRecordRepository playerLpRecordRepository; // Database-Repository für LP-Record-Persistierung


    @Autowired // Dependency Injection durch Spring
    public PlayerLpRecordService(PlayerLpRecordRepository playerLpRecordRepository) { // Konstruktor mit Repository-Injection
        this.playerLpRecordRepository = playerLpRecordRepository; // Weist das injizierte Repository der Instanzvariable zu
    }


    @Transactional // Transaktions-Annotation: Alle Database-Operationen in dieser Methode werden in einer Transaktion ausgeführt
    public void savePlayerLpRecords(String puuid, List<LeagueEntryDTO> leagueEntries, Instant timestamp) { // Öffentliche Methode zum Speichern von LP-Records in Datenbank
        Instant ts = timestamp != null ? timestamp : Instant.now(); // Null-Safe: Verwendet übergebenen Zeitstempel oder aktuellen Zeitpunkt
        for (LeagueEntryDTO entry : leagueEntries) { // Iteriert über alle Liga-Einträge (SoloQ, FlexQ, etc.)
            if ("RANKED_SOLO_5x5".equals(entry.getQueueType()) || "RANKED_FLEX_SR".equals(entry.getQueueType())) { // Filtert nur Ranked-Queues (SoloQ und FlexQ)
                PlayerLpRecord record = new PlayerLpRecord( // Erstellt neues LP-Record-Entity
                        puuid, // Spieler-PUUID
                        entry.getQueueType(), // Queue-Typ (RANKED_SOLO_5x5 oder RANKED_FLEX_SR)
                        ts, // Zeitstempel des Snapshots
                        entry.getLeaguePoints(), // Aktuelle LP-Anzahl
                        entry.getTier(), // Tier (z.B. DIAMOND, EMERALD)
                        entry.getRank() // Rang (I, II, III, IV)
                );
                playerLpRecordRepository.save(record); // Database-Operation: Speichert LP-Record in Datenbank
                logger.debug("Saved LP record for puuid {}, queue {}: {} LP at {}", maskPuuid(puuid), entry.getQueueType(), entry.getLeaguePoints(), ts); // Debug-Logging für gespeicherten Record
            }
        }
    }


    public void calculateAndSetLpChangesForMatches(Summoner summoner, List<MatchV5Dto> matchHistory) { // Öffentliche Methode zur Berechnung und Zuordnung von LP-Änderungen zu Matches (Business-Logik)
        if (summoner == null || !StringUtils.hasText(summoner.getPuuid()) || matchHistory == null || matchHistory.isEmpty()) { // Validierung: Prüft ob alle benötigten Daten vorhanden
            return; // Beendet Methode vorzeitig bei ungültigen Eingaben (Silent Return)
        }

        Map<String, List<PlayerLpRecord>> recordsByQueue = preloadRankedRecords(summoner.getPuuid(), matchHistory); // Database-Operation: Lädt alle relevanten LP-Records aus Datenbank vorab (Performance-Optimierung)

        for (MatchV5Dto match : matchHistory) { // Iteriert über alle Matches in der Historie
            if (match == null || match.getInfo() == null) { // Prüft ob Match-Daten vollständig sind
                continue; // Überspringt ungültige Matches
            }

            String queueTypeForDbQuery = resolveRankedQueueType(match.getInfo().getQueueId()); // Business-Logik: Konvertiert Queue-ID zu Queue-Typ-String (420→RANKED_SOLO_5x5)
            if (queueTypeForDbQuery == null) { // Prüft ob Queue ein Ranked-Modus ist
                continue; // Überspringt Non-Ranked-Matches (Normal/ARAM/etc.)
            }

            long endMillis = match.getInfo().getGameEndTimestamp(); // Holt Match-End-Zeitstempel in Millisekunden
            if (endMillis <= 0L) { // Validierung: Prüft ob Zeitstempel vorhanden
                logger.debug("Skipping LP change for match {} due to missing/invalid gameEndTimestamp.", safeMatchId(match)); // Debug-Logging für übersprungene Matches
                continue; // Überspringt Matches ohne gültigen Zeitstempel
            }
            Instant matchEndTime = Instant.ofEpochMilli(endMillis); // Konvertiert Millisekunden zu Instant-Objekt

            try { // Try-Block für fehlertolerante Verarbeitung (ein Fehler stoppt nicht alle Matches)
                List<PlayerLpRecord> records = recordsByQueue.getOrDefault(queueTypeForDbQuery, Collections.emptyList()); // Holt vorgeladene LP-Records für diese Queue
                if (records.isEmpty()) { // Prüft ob LP-Historie vorhanden ist
                    logger.debug("No LP history available for puuid {} queue {}.", maskPuuid(summoner.getPuuid()), queueTypeForDbQuery); // Debug-Info für fehlende LP-Historie
                    continue; // Überspringt Match wenn keine LP-Daten vorhanden
                }

                PlayerLpRecord recordBefore = findRecordBefore(records, matchEndTime); // Business-Logik: Findet LP-Record VOR Match-Ende (Binärsuche)
                PlayerLpRecord recordAfter = findRecordAfter(records, matchEndTime); // Business-Logik: Findet LP-Record NACH Match-Ende (Binärsuche)

                if (recordBefore == null || recordAfter == null) { // Validierung: Prüft ob beide Records gefunden wurden
                    logger.debug("LP records before or after match {} not found for PUUID {} and queue {}. Cannot calculate LP change.", // Debug-Info für fehlende Records
                            safeMatchId(match), maskPuuid(summoner.getPuuid()), queueTypeForDbQuery);
                    continue; // Überspringt Match wenn LP-Records fehlen
                }

                if (!recordAfter.getTimestamp().isAfter(matchEndTime)) { // Prüft ob "After"-Record tatsächlich nach Match-Ende liegt
                    Duration gap = Duration.between(recordAfter.getTimestamp(), matchEndTime); // Berechnet Zeitdifferenz
                    if (gap.compareTo(MATCH_END_TOLERANCE) > 0) { // Prüft ob Differenz über Toleranzgrenze liegt (>10 Minuten)
                        logger.debug("LP record after match {} for PUUID {} (queue {}) is {} minutes before match end time {} – skipping.", // Debug-Info für zeitlich inkonsistente Records
                                safeMatchId(match), maskPuuid(summoner.getPuuid()), queueTypeForDbQuery, gap.toMinutes(), matchEndTime);
                        continue; // Überspringt Match bei zu großer Zeitdifferenz
                    }
                }

                int lpBefore = recordBefore.getLeaguePoints(); // Holt LP vor Match
                int lpAfter = recordAfter.getLeaguePoints(); // Holt LP nach Match
                int lpChange = lpAfter - lpBefore; // Business-Logik: Berechnet LP-Differenz (kann positiv/negativ/0 sein)

                if (!Objects.equals(recordBefore.getTier(), recordAfter.getTier()) || !Objects.equals(recordBefore.getRank(), recordAfter.getRank())) { // Prüft ob Tier/Rang sich geändert hat (Promotion/Demotion)
                    logger.warn("Tier/Rank changed for match {}. PUUID: {}. Before: {} {} {} LP, After: {} {} {} LP. LP Change calculation might be inaccurate or represent promotion/demotion.", // Warnung bei Tier/Rang-Wechsel
                            safeMatchId(match), maskPuuid(summoner.getPuuid()),
                            recordBefore.getTier(), recordBefore.getRank(), recordBefore.getLeaguePoints(),
                            recordAfter.getTier(), recordAfter.getRank(), recordAfter.getLeaguePoints());
                    match.getInfo().setLpChange(null); // Setzt LP-Änderung auf null bei Promotion/Demotion (ungenau)
                } else { // Tier/Rang unverändert - normale LP-Änderung
                    match.getInfo().setLpChange(lpChange); // Business-Logik: Setzt berechnete LP-Änderung im Match-Objekt
                }
            } catch (Exception e) { // Fängt Fehler bei einzelnem Match ab
                logger.error("Error calculating LP change for match {} PUUID {}: {}", safeMatchId(match), maskPuuid(summoner.getPuuid()), e.getMessage(), e); // Error-Logging mit Stack-Trace
            }
        }
    }

    private Map<String, List<PlayerLpRecord>> preloadRankedRecords(String puuid, List<MatchV5Dto> matchHistory) { // Private Methode zum Vorladen aller relevanten LP-Records aus DB (Performance-Optimierung)
        Set<String> queues = new HashSet<>(); // Set zur Sammlung aller benötigten Queue-Typen (ohne Duplikate)
        for (MatchV5Dto match : matchHistory) { // Iteriert über alle Matches
            if (match == null || match.getInfo() == null) { // Prüft ob Match-Daten vorhanden
                continue; // Überspringt ungültige Matches
            }
            String queue = resolveRankedQueueType(match.getInfo().getQueueId()); // Konvertiert Queue-ID zu Queue-Typ
            if (queue != null) { // Prüft ob es ein Ranked-Queue ist
                queues.add(queue); // Fügt Queue-Typ zum Set hinzu (Duplikate automatisch ignoriert)
            }
        }

        Map<String, List<PlayerLpRecord>> cache = new HashMap<>(); // Erstellt Cache-Map für LP-Records pro Queue
        for (String queue : queues) { // Iteriert über alle benötigten Queues
            try { // Try-Block für fehlertolerante DB-Abfragen
                List<PlayerLpRecord> recordsDesc = playerLpRecordRepository.findByPuuidAndQueueTypeOrderByTimestampDesc(puuid, queue); // Database-Operation: Lädt LP-Records für PUUID und Queue sortiert nach Zeitstempel absteigend
                if (recordsDesc.isEmpty()) { // Prüft ob Records vorhanden
                    cache.put(queue, Collections.emptyList()); // Speichert leere Liste im Cache
                } else { // Records vorhanden
                    List<PlayerLpRecord> ascending = new ArrayList<>(recordsDesc); // Erstellt Kopie der Liste
                    Collections.reverse(ascending); // Kehrt Sortierung um zu aufsteigend (für Binärsuche benötigt)
                    cache.put(queue, ascending); // Speichert aufsteigend sortierte Liste im Cache
                }
            } catch (Exception e) { // Fängt DB-Fehler ab
                logger.error("Failed to preload LP records for puuid {} queue {}: {}", maskPuuid(puuid), queue, e.getMessage(), e); // Error-Logging
                cache.put(queue, Collections.emptyList()); // Speichert leere Liste bei Fehler (Fallback)
            }
        }
        return cache; // Gibt gefüllten Cache zurück
    }

    private String resolveRankedQueueType(int queueId) { // Private Methode zur Konvertierung Queue-ID → Queue-Typ-String
        return switch (queueId) { // Switch-Expression für Queue-ID-Mapping
            case 420 -> "RANKED_SOLO_5x5"; // SoloQ
            case 440 -> "RANKED_FLEX_SR"; // FlexQ
            default -> null; // Alle anderen Queues werden ignoriert (Normal/ARAM/etc.)
        };
    }

    private PlayerLpRecord findRecordBefore(List<PlayerLpRecord> records, Instant timestamp) { // Private Methode zur Suche des letzten LP-Records VOR einem Zeitpunkt (Binärsuche)
        int low = 0; // Untere Grenze für Binärsuche
        int high = records.size() - 1; // Obere Grenze für Binärsuche
        PlayerLpRecord candidate = null; // Kandidat für besten Match
        while (low <= high) { // Binärsuche-Schleife
            int mid = (low + high) >>> 1; // Berechnet mittleren Index (>>> 1 = Division durch 2, Overflow-sicher)
            PlayerLpRecord current = records.get(mid); // Holt Record an mittlerer Position
            if (current.getTimestamp().isBefore(timestamp)) { // Prüft ob Record vor Zeitstempel liegt
                candidate = current; // Aktualisiert Kandidat (näher am gesuchten Zeitpunkt)
                low = mid + 1; // Sucht in oberer Hälfte weiter
            } else { // Record liegt nach oder genau am Zeitstempel
                high = mid - 1; // Sucht in unterer Hälfte weiter
            }
        }
        return candidate; // Gibt letzten Record vor Zeitstempel zurück (oder null wenn keiner gefunden)
    }

    private PlayerLpRecord findRecordAfter(List<PlayerLpRecord> records, Instant timestamp) { // Private Methode zur Suche des ersten LP-Records NACH einem Zeitpunkt (Binärsuche)
        int low = 0; // Untere Grenze für Binärsuche
        int high = records.size() - 1; // Obere Grenze für Binärsuche
        PlayerLpRecord candidate = null; // Kandidat für besten Match
        while (low <= high) { // Binärsuche-Schleife
            int mid = (low + high) >>> 1; // Berechnet mittleren Index (>>> 1 = Division durch 2, Overflow-sicher)
            PlayerLpRecord current = records.get(mid); // Holt Record an mittlerer Position
            if (current.getTimestamp().isBefore(timestamp)) { // Prüft ob Record vor Zeitstempel liegt
                low = mid + 1; // Sucht in oberer Hälfte weiter
            } else { // Record liegt nach oder genau am Zeitstempel
                candidate = current; // Aktualisiert Kandidat (näher am gesuchten Zeitpunkt)
                high = mid - 1; // Sucht in unterer Hälfte weiter
            }
        }
        return candidate; // Gibt ersten Record nach Zeitstempel zurück (oder null wenn keiner gefunden)
    }

    private static String maskPuuid(String puuid) { // Private Utility-Methode zum Maskieren von PUUIDs für Logging (Datenschutz)
        if (puuid == null) return "(null)"; // Gibt "(null)" für null-Werte zurück
        int len = puuid.length(); // Ermittelt Länge der PUUID
        if (len <= 10) return "***"; // Gibt "***" für sehr kurze PUUIDs zurück (Vollmaskierung)
        return puuid.substring(0, 6) + "..." + puuid.substring(len - 4); // Maskiert PUUID: Zeigt erste 6 und letzte 4 Zeichen (z.B. "abc123...xyz9")
    }

    private static String safeMatchId(MatchV5Dto match) { // Private Utility-Methode zum sicheren Extrahieren der Match-ID für Logging
        try { // Try-Block für null-sichere Extraktion
            if (match != null && match.getMetadata() != null && match.getMetadata().getMatchId() != null) { // Prüft ob alle benötigten Objekte vorhanden
                return match.getMetadata().getMatchId(); // Gibt Match-ID zurück
            }
        } catch (Exception ignored) {} // Ignoriert alle Fehler (NullPointer etc.)
        return "(unknown)"; // Gibt Fallback-String zurück wenn Match-ID nicht verfügbar
    }
} // Ende der PlayerLpRecordService-Klasse
