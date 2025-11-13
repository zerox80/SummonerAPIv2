package com.zerox80.riotapi.service; // Paketdeklaration für den Service-Layer der Riot API

import com.zerox80.riotapi.client.RiotApiClient; // Import des Riot API Clients für externe API-Aufrufe
import com.zerox80.riotapi.model.*; // Import aller Model-Klassen (DTOs und Entities)
import com.zerox80.riotapi.service.PlayerLpRecordService; // Import des Services für LP-Historie-Verwaltung
import org.slf4j.Logger; // Import für Logging-Schnittstelle
import org.slf4j.LoggerFactory; // Import für Logger-Factory zur Logger-Instanziierung
import org.springframework.beans.factory.annotation.Autowired; // Import für Dependency Injection
import org.springframework.beans.factory.annotation.Value; // Import für Property-Injection (nicht verwendet in diesem Service)
import org.springframework.cache.annotation.Cacheable; // Import für Cache-Annotation zur Ergebnis-Zwischenspeicherung
import org.springframework.stereotype.Service; // Import für Service-Komponenten-Annotation
import org.springframework.transaction.annotation.Transactional; // Import für Transaktionsverwaltung (nicht verwendet in diesem Service)
import org.springframework.util.StringUtils; // Import für String-Utility-Funktionen wie hasText()

import java.io.IOException; // Import für I/O-Exceptions
import java.time.Instant; // Import für Zeitstempel-Verwaltung
import java.util.List; // Import für Listen-Collections
import java.util.Collections; // Import für Collection-Utility-Methoden wie emptyList()
import java.util.ArrayList; // Import für ArrayList-Implementierung
import java.util.Map; // Import für Map-Interface
import java.util.LinkedHashMap; // Import für geordnete Map-Implementierung
import java.util.concurrent.CompletableFuture; // Import für asynchrone/nicht-blockierende Operationen
import java.util.stream.Collectors; // Import für Stream-Sammler (z.B. toList())
import java.util.concurrent.ConcurrentHashMap; // Import für thread-sichere Map (nicht verwendet)
import java.util.stream.Stream; // Import für Stream-API
import java.util.Optional; // Import für Optional-Container (nicht verwendet)
import java.util.Locale; // Import für Sprach-/Regions-Einstellungen
import com.zerox80.riotapi.util.ListUtils; // Import für List-Utility-Funktionen (z.B. partition)


@Service // Markiert diese Klasse als Spring Service-Komponente für Dependency Injection
public class RiotApiService { // Service-Klasse für Riot API-Operationen und Business-Logik

    private static final Logger logger = LoggerFactory.getLogger(RiotApiService.class); // Logger-Instanz für Logging in diesem Service
    private final RiotApiClient riotApiClient; // Client für HTTP-Aufrufe an die Riot API
    private final PlayerLpRecordService playerLpRecordService; // Service für LP-Historie und LP-Änderungs-Berechnungen


    @Autowired // Dependency Injection durch Spring
    public RiotApiService(RiotApiClient riotApiClient, // Injiziert den Riot API Client
                          PlayerLpRecordService playerLpRecordService) { // Injiziert den LP-Record Service
        this.riotApiClient = riotApiClient; // Weist den injizierten Client der Instanzvariable zu
        this.playerLpRecordService = playerLpRecordService; // Weist den injizierten Service der Instanzvariable zu
    }


    public CompletableFuture<List<MatchV5Dto>> getMatchHistoryPaged(String puuid, int start, int count) { // Öffentliche Methode zum Abrufen von Match-Historie mit Pagination (asynchron)
        if (!StringUtils.hasText(puuid)) { // Validierung: Prüft ob PUUID nicht null/leer ist
            logger.error("Error: PUUID cannot be empty when fetching match history."); // Loggt Fehler wenn PUUID fehlt
            return CompletableFuture.completedFuture(Collections.emptyList()); // Gibt sofort abgeschlossenes Future mit leerer Liste zurück
        }
        if (count <= 0) { // Validierung: Prüft ob Anzahl der Matches positiv ist
            logger.error("Error: Count must be positive."); // Loggt Fehler wenn count ungültig
            return CompletableFuture.completedFuture(Collections.emptyList()); // Gibt sofort abgeschlossenes Future mit leerer Liste zurück
        }
        if (start < 0) start = 0; // Normalisierung: Setzt negativen Start-Index auf 0

        final int from = start; // Final-Variablen für Lambda-Verwendung (Start-Index)
        final int limit = count; // Final-Variablen für Lambda-Verwendung (Anzahl Matches)
        logger.info("Fetching paged match IDs for PUUID: {}, start={}, count={}...", maskPuuid(puuid), from, limit); // Loggt Info über bevorstehenden API-Aufruf
        return riotApiClient.getMatchIdsByPuuid(puuid, from, limit) // Asynchroner API-Aufruf zum Abrufen von Match-IDs
                .thenCompose(matchIds -> { // Verkettung: Wird ausgeführt wenn Match-IDs abgerufen wurden
                    if (matchIds == null || matchIds.isEmpty()) { // Prüft ob keine Match-IDs zurückgegeben wurden
                        return CompletableFuture.completedFuture(Collections.<MatchV5Dto>emptyList()); // Gibt leere Liste zurück wenn keine IDs vorhanden
                    }
                    List<List<String>> batches = ListUtils.partition(matchIds, 5); // Teilt Match-IDs in Batches à 5 auf zur parallelen Verarbeitung
                    List<CompletableFuture<List<MatchV5Dto>>> batchFutures = batches.stream() // Stream über alle Batches
                            .map(this::fetchMatchBatch) // Ruft für jeden Batch asynchron die Match-Details ab
                            .collect(Collectors.toList()); // Sammelt alle Futures in einer Liste
                    CompletableFuture<Void> allDone = CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0])); // Kombiniert alle Batch-Futures zu einem einzigen Future das wartet bis alle abgeschlossen sind
                    return allDone.thenApply(v -> batchFutures.stream() // Nach Abschluss aller Batches: Transformiert Ergebnisse
                            .flatMap(f -> { // FlatMap über alle Batch-Futures
                                List<MatchV5Dto> list = f.join(); // Wartet auf Ergebnis des Batch-Futures (blockierend aber sicher da allDone bereits abgeschlossen)
                                return list != null ? list.stream() : Stream.<MatchV5Dto>empty(); // Erstellt Stream aus Liste oder leeren Stream wenn null
                            })
                            .filter(java.util.Objects::nonNull) // Filtert null-Werte aus dem Stream
                            .collect(Collectors.toList())); // Sammelt alle Matches in einer finalen Liste
                })
                .exceptionally(ex -> { // Exception-Handler für asynchrone Fehler
                    logger.error("Error fetching paged match history for puuid {}: {}", maskPuuid(puuid), ex.getMessage(), ex); // Loggt Fehler mit Stack-Trace
                    return Collections.<MatchV5Dto>emptyList(); // Gibt leere Liste bei Fehler zurück (Fallback)
                });
    }


    public CompletableFuture<Summoner> getSummonerByRiotId(String gameName, String tagLine) { // Öffentliche Methode zum Abrufen von Summoner-Daten anhand Riot-ID (asynchron)
        String trimmedGameName = gameName != null ? gameName.trim() : null; // Entfernt führende/abschließende Leerzeichen vom GameName (null-safe)
        String trimmedTagLine = tagLine != null ? tagLine.trim() : null; // Entfernt führende/abschließende Leerzeichen vom TagLine (null-safe)

        if (!StringUtils.hasText(trimmedGameName) || !StringUtils.hasText(trimmedTagLine)) { // Validierung: Prüft ob beide Felder nicht leer sind
            logger.error("Error: Game name and tag line cannot be empty."); // Loggt Fehler wenn Eingaben ungültig
            return CompletableFuture.completedFuture(null); // Gibt sofort abgeschlossenes Future mit null zurück
        }

        String normalizedGameName = trimmedGameName.replaceAll("\\s+", " "); // Normalisierung: Ersetzt mehrere Leerzeichen durch einzelnes Leerzeichen
        String normalizedTagLine = trimmedTagLine.replaceAll("\\s+", " "); // Normalisierung: Ersetzt mehrere Leerzeichen durch einzelnes Leerzeichen
        String cacheFriendlyGameName = normalizedGameName.toLowerCase(Locale.ROOT); // Konvertiert zu Kleinbuchstaben für Cache-Konsistenz
        String cacheFriendlyTagLine = normalizedTagLine.toLowerCase(Locale.ROOT); // Konvertiert zu Kleinbuchstaben für Cache-Konsistenz

        logger.info("Searching for account: {}#{}...", normalizedGameName, normalizedTagLine); // Loggt Info über Suchanfrage
        return riotApiClient.getAccountByRiotId(cacheFriendlyGameName, cacheFriendlyTagLine) // Asynchroner API-Aufruf zum Abrufen von Account-Daten
                .thenCompose(account -> { // Verkettung: Wird ausgeführt wenn Account abgerufen wurde
                    if (account != null && StringUtils.hasText(account.getPuuid())) { // Prüft ob Account existiert und PUUID vorhanden ist
                        logger.info("Account found, PUUID: {}. Fetching summoner data...", maskPuuid(account.getPuuid())); // Loggt erfolgreiche Account-Abruf
                        return riotApiClient.getSummonerByPuuid(account.getPuuid()) // Asynchroner API-Aufruf zum Abrufen von Summoner-Details via PUUID
                                .thenApply(summoner -> { // Transformation: Wird ausgeführt wenn Summoner abgerufen wurde
                                    if (summoner != null) { // Prüft ob Summoner-Daten vorhanden sind
                                        if (StringUtils.hasText(account.getGameName())) { // Prüft ob GameName im Account-Objekt vorhanden
                                            summoner.setName(account.getGameName()); // Setzt den GameName aus Account-Daten (bevorzugte Quelle)
                                        } else { // Fallback wenn GameName in Account fehlt
                                            summoner.setName(normalizedGameName); // Verwendet den ursprünglich übergebenen GameName
                                            logger.warn("Warning: gameName is missing from AccountDto for PUUID: {}. Using provided gameName for Summoner object.", maskPuuid(account.getPuuid())); // Warnung über fehlenden GameName in API-Response
                                        }
                                    }
                                    return summoner; // Gibt modifizierten Summoner zurück
                                });
                    } else { // Account wurde nicht gefunden oder PUUID fehlt
                        logger.info("Account for {}#{} not found or PUUID is missing.", trimmedGameName, trimmedTagLine); // Loggt Info über nicht gefundenen Account
                        return CompletableFuture.completedFuture(null); // Gibt sofort abgeschlossenes Future mit null zurück
                    }
                }).exceptionally(ex -> { // Exception-Handler für asynchrone Fehler
                    logger.error("Error fetching summoner data for {}#{}: {}", normalizedGameName, normalizedTagLine, ex.getMessage(), ex); // Loggt Fehler mit Details
                    return null; // Gibt null bei Fehler zurück (Fallback)
                });
    }


    public CompletableFuture<List<LeagueEntryDTO>> getLeagueEntries(String puuid) { // Öffentliche Methode zum Abrufen von Ranked-Liga-Einträgen (asynchron)
        if (!StringUtils.hasText(puuid)) { // Validierung: Prüft ob PUUID nicht null/leer ist
            logger.error("Error: PUUID cannot be empty."); // Loggt Fehler wenn PUUID fehlt
            return CompletableFuture.completedFuture(Collections.emptyList()); // Gibt sofort abgeschlossenes Future mit leerer Liste zurück
        }
        logger.info("Fetching league entries (by PUUID) for PUUID: {}...", maskPuuid(puuid)); // Loggt Info über bevorstehenden API-Aufruf

        return riotApiClient.getLeagueEntriesByPuuid(puuid) // Asynchroner API-Aufruf zum Abrufen von Liga-Einträgen via PUUID
                .thenApply(leagueEntries -> { // Transformation: Wird ausgeführt wenn Liga-Einträge abgerufen wurden
                    if (leagueEntries != null && !leagueEntries.isEmpty()) { // Prüft ob Liga-Einträge vorhanden sind
                        Instant now = Instant.now(); // Erstellt aktuellen Zeitstempel für LP-Record-Speicherung
                        playerLpRecordService.savePlayerLpRecords(puuid, leagueEntries, now); // Database-Operation: Speichert LP-Records in Datenbank für spätere LP-Änderungs-Berechnungen
                    }
                    return leagueEntries; // Gibt Liga-Einträge zurück (unverändert durchgereicht)
                })
                .exceptionally(ex -> { // Exception-Handler für asynchrone Fehler
                    logger.error("Error fetching or processing league entries for puuid {}: {}", // Loggt Fehler bei API-Aufruf oder DB-Speicherung
                            maskPuuid(puuid), ex.getMessage(), ex);
                    return Collections.emptyList(); // Gibt leere Liste bei Fehler zurück (Fallback)
                });
    }


    @Cacheable(value = "matchHistory", key = "#puuid + '-' + #numberOfMatches") // Cache-Annotation: Ergebnisse werden im "matchHistory"-Cache gespeichert mit kombiniertem Key aus PUUID und Anzahl
    public CompletableFuture<List<MatchV5Dto>> getMatchHistory(String puuid, int numberOfMatches) { // Öffentliche Methode zum Abrufen von Match-Historie (gecacht, asynchron)
        if (!StringUtils.hasText(puuid)) { // Validierung: Prüft ob PUUID nicht null/leer ist
            logger.error("Error: PUUID cannot be empty when fetching match history."); // Loggt Fehler wenn PUUID fehlt
            return CompletableFuture.completedFuture(Collections.emptyList()); // Gibt sofort abgeschlossenes Future mit leerer Liste zurück
        }
        if (numberOfMatches <= 0) { // Validierung: Prüft ob Anzahl der Matches positiv ist
            logger.error("Error: Number of matches to fetch must be positive."); // Loggt Fehler wenn Anzahl ungültig
            return CompletableFuture.completedFuture(Collections.emptyList()); // Gibt sofort abgeschlossenes Future mit leerer Liste zurück
        }

        logger.info("Fetching last {} match IDs for PUUID: {}...", numberOfMatches, maskPuuid(puuid)); // Loggt Info über bevorstehenden API-Aufruf
        return riotApiClient.getMatchIdsByPuuid(puuid, numberOfMatches) // Asynchroner API-Aufruf zum Abrufen von Match-IDs
                .thenCompose(matchIds -> { // Verkettung: Wird ausgeführt wenn Match-IDs abgerufen wurden
                    if (matchIds.isEmpty()) { // Prüft ob keine Match-IDs zurückgegeben wurden
                        logger.info("No match IDs found for PUUID: {}", maskPuuid(puuid)); // Loggt Info über fehlende Matches
                        return CompletableFuture.completedFuture(Collections.<MatchV5Dto>emptyList()); // Gibt leere Liste zurück wenn keine IDs vorhanden
                    }
                    logger.info("Fetching details for {} matches in batches...", matchIds.size()); // Loggt Info über Batch-Verarbeitung

                    List<List<String>> batches = ListUtils.partition(matchIds, 5); // Teilt Match-IDs in Batches à 5 auf zur parallelen Verarbeitung (Rate-Limiting-Schutz)
                    List<CompletableFuture<List<MatchV5Dto>>> batchFutures = batches.stream() // Stream über alle Batches
                            .map(this::fetchMatchBatch) // Ruft für jeden Batch asynchron die Match-Details ab
                            .collect(Collectors.toList()); // Sammelt alle Batch-Futures in einer Liste

                    CompletableFuture<Void> allDone = CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0])); // Kombiniert alle Batch-Futures zu einem einzigen Future das wartet bis alle abgeschlossen sind
                    return allDone.thenApply(v -> batchFutures.stream() // Nach Abschluss aller Batches: Transformiert Ergebnisse
                            .flatMap(f -> { // FlatMap über alle Batch-Futures
                                List<MatchV5Dto> list = f.join(); // Wartet auf Ergebnis des Batch-Futures (blockierend aber sicher da allDone bereits abgeschlossen)
                                return list != null ? list.stream() : Stream.<MatchV5Dto>empty(); // Erstellt Stream aus Liste oder leeren Stream wenn null
                            })
                            .filter(java.util.Objects::nonNull) // Filtert null-Werte aus dem Stream (fehlerhafte Matches)
                            .collect(Collectors.toList())); // Sammelt alle Matches in einer finalen Liste
                }).exceptionally(ex -> { // Exception-Handler für asynchrone Fehler
                    logger.error("Error fetching match history for puuid {}: {}", maskPuuid(puuid), ex.getMessage(), ex); // Loggt Fehler mit Stack-Trace
                    return Collections.emptyList(); // Gibt leere Liste bei Fehler zurück (Fallback)
                });
    }

    private CompletableFuture<List<MatchV5Dto>> fetchMatchBatch(List<String> matchIdBatch) { // Private Hilfsmethode zum Abrufen eines Batches von Match-Details (asynchron)
        List<CompletableFuture<MatchV5Dto>> matchDetailFutures = matchIdBatch.stream() // Stream über alle Match-IDs im Batch
            .map(matchId -> riotApiClient.getMatchDetails(matchId) // Asynchroner API-Aufruf für jede Match-ID
                .exceptionally(ex -> { // Exception-Handler für einzelne Match-Abfragen
                    logger.error("Error fetching details for match ID {}: {}", matchId, ex.getMessage(), ex); // Loggt Fehler für fehlgeschlagene Match-Abfrage
                    return null; // Gibt null zurück bei Fehler (wird später ausgefiltert)
                }))
            .collect(Collectors.toList()); // Sammelt alle Match-Detail-Futures in einer Liste

        return CompletableFuture.allOf(matchDetailFutures.toArray(new CompletableFuture[0])) // Kombiniert alle Match-Detail-Futures zu einem einzigen Future
            .thenApply(v -> matchDetailFutures.stream() // Nach Abschluss aller Match-Details: Transformiert Ergebnisse
                .map(CompletableFuture::join) // Extrahiert Ergebnis aus jedem Future (blockierend aber sicher)
                .filter(java.util.Objects::nonNull) // Filtert null-Werte aus (fehlgeschlagene Abfragen)
                .collect(Collectors.toList())); // Sammelt erfolgreiche Matches in Liste
    }


    public Map<String, Long> getChampionPlayCounts(List<MatchV5Dto> matches, String searchedPuuid) { // Öffentliche Methode zur Berechnung von Champion-Spielhäufigkeiten für einen Spieler
        if (matches == null || matches.isEmpty() || !StringUtils.hasText(searchedPuuid)) { // Validierung: Prüft ob Matches und PUUID vorhanden sind
            return Collections.emptyMap(); // Gibt leere Map zurück bei ungültiger Eingabe
        }

        return matches.stream() // Stream über alle Matches
                .filter(match -> match != null && match.getInfo() != null && match.getInfo().getParticipants() != null) // Filtert null-Matches und Matches ohne Teilnehmer-Daten
                .flatMap(match -> match.getInfo().getParticipants().stream()) // FlatMap: Erstellt Stream aller Teilnehmer aus allen Matches
                .filter(participant -> participant != null && searchedPuuid.equals(participant.getPuuid()) && StringUtils.hasText(participant.getChampionName())) // Filtert nach dem gesuchten Spieler (PUUID-Match) und gültigem Champion-Namen
                .collect(Collectors.groupingBy(ParticipantDto::getChampionName, Collectors.counting())) // Business-Logik: Gruppiert nach Champion-Name und zählt Vorkommen
                .entrySet().stream() // Stream über die Champion→Count Map-Einträge
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // Sortierung: Nach Spielanzahl absteigend (meistgespielt zuerst)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)); // Sammelt in LinkedHashMap um Sortierung beizubehalten
    }


    public List<SummonerSuggestionDTO> getSummonerSuggestions(String partialName, Map<String, SummonerSuggestionDTO> userHistoryDtoMap) { // Öffentliche Methode zum Abrufen von Summoner-Vorschlägen basierend auf Suchhistorie
        Stream<Map.Entry<String, SummonerSuggestionDTO>> stream; // Deklariert Stream für Map-Einträge
        Map<String, SummonerSuggestionDTO> historyToUse = (userHistoryDtoMap != null) ? userHistoryDtoMap : Collections.emptyMap(); // Null-Safe: Verwendet übergebene History oder leere Map

        if (!StringUtils.hasText(partialName)) { // Prüft ob kein Suchtext eingegeben wurde
            stream = historyToUse.entrySet().stream(); // Verwendet alle History-Einträge ohne Filter
        } else { // Suchtext wurde eingegeben
            String lowerPartialName = partialName.toLowerCase(Locale.ROOT); // Normalisiert Suchtext zu Kleinbuchstaben
            stream = historyToUse.entrySet().stream() // Stream über alle History-Einträge
                    .filter(entry -> entry.getKey().startsWith(lowerPartialName)); // Filtert nach Einträgen die mit Suchtext beginnen (Prefix-Matching)
        }

        List<SummonerSuggestionDTO> out = stream // Weiterverarbeitung des gefilterten Streams
                .limit(10) // Business-Logik: Limitiert auf maximal 10 Vorschläge (Performance-Optimierung)
                .map(Map.Entry::getValue) // Extrahiert nur die DTO-Werte aus den Map-Einträgen
                .distinct() // Entfernt Duplikate aus den Vorschlägen
                .collect(Collectors.toList()); // Sammelt Ergebnisse in Liste
        // Enrich missing icon URLs to satisfy frontend expectation
        out.forEach(dto -> { // Iteriert über alle Vorschläge zur Anreicherung
            if (dto != null && !StringUtils.hasText(dto.getProfileIconUrl())) { // Prüft ob DTO existiert aber Icon-URL fehlt
                int iconId = dto.getProfileIconId(); // Holt Icon-ID aus DTO
                if (iconId <= 0) { // Prüft ob Icon-ID ungültig ist
                    dto.setProfileIconUrl(null); // Setzt URL auf null bei ungültiger ID
                    return; // Beendet diese Iteration vorzeitig
                }
                try { // Try-Block für URL-Generierung
                    dto.setProfileIconUrl(riotApiClient.getProfileIconUrl(iconId)); // Business-Logik: Generiert Icon-URL aus Icon-ID via Client
                } catch (Exception ignored) {} // Ignoriert Fehler bei URL-Generierung (Fallback auf null)
            }
        });
        return out; // Gibt angereicherte Vorschlagsliste zurück
    }


    public CompletableFuture<SummonerProfileData> getSummonerProfileDataAsync(String gameName, String tagLine) { // Öffentliche Methode zum Abrufen vollständiger Summoner-Profildaten (asynchron, komplex)
        return getSummonerByRiotId(gameName, tagLine) // Asynchroner Aufruf: Holt zuerst Basis-Summoner-Daten
                .thenCompose(summoner -> { // Verkettung: Wird ausgeführt wenn Summoner abgerufen wurde
                    if (summoner == null || !StringUtils.hasText(summoner.getPuuid())) { // Validierung: Prüft ob Summoner gefunden und PUUID vorhanden
                        logger.warn("Summoner not found or PUUID is missing for {}#{}", gameName, tagLine); // Warnung bei nicht gefundenem Summoner
                        return CompletableFuture.completedFuture(new SummonerProfileData("Summoner not found or PUUID missing.")); // Gibt Fehler-Profildaten zurück
                    }

                    String displayRiotId = summoner.getName() + "#" + tagLine; // Business-Logik: Erstellt Anzeigename im Format Name#Tag
                    String iconUrl = riotApiClient.getProfileIconUrl(summoner.getProfileIconId()); // Generiert Profil-Icon-URL
                    SummonerSuggestionDTO suggestionDTO = new SummonerSuggestionDTO(displayRiotId, summoner.getProfileIconId(), summoner.getSummonerLevel(), iconUrl); // Erstellt Suggestion-DTO für Suchhistorie

                    // Fetch league entries (by PUUID) and match history concurrently
                    CompletableFuture<List<LeagueEntryDTO>> leagueEntriesFuture = // Asynchroner API-Aufruf: Holt Liga-Einträge parallel
                            riotApiClient.getLeagueEntriesByPuuid(summoner.getPuuid()) // API-Aufruf für Liga-Daten
                                    .thenApply(leagueEntries -> { // Transformation nach erfolgreichem Abruf
                                        if (leagueEntries != null && !leagueEntries.isEmpty()) { // Prüft ob Liga-Daten vorhanden
                                            Instant now = Instant.now(); // Erstellt Zeitstempel
                                            playerLpRecordService.savePlayerLpRecords(summoner.getPuuid(), leagueEntries, now); // Database-Operation: Speichert LP-Snapshot für LP-Verlaufs-Tracking
                                        }
                                        return leagueEntries; // Gibt Liga-Einträge zurück
                                    })
                                    .exceptionally(ex -> { // Exception-Handler für Liga-Abruf
                                        logger.error("Error fetching league entries for puuid {}: {}", // Loggt Fehler
                                                summoner.getPuuid(), ex.getMessage(), ex);
                                        return Collections.emptyList(); // Gibt leere Liste bei Fehler zurück
                                    });
                    // Load fewer matches initially for faster first render; client can request more via pagination
                    CompletableFuture<List<MatchV5Dto>> matchHistoryFuture = getMatchHistory(summoner.getPuuid(), 10); // Asynchroner Aufruf: Holt nur 10 Matches initial (Performance-Optimierung für schnelle Ladezeiten)

                    return CompletableFuture.allOf(leagueEntriesFuture, matchHistoryFuture) // Kombiniert beide parallelen Futures (Liga + Matches)
                            .thenApply(v -> { // Transformation: Wird ausgeführt wenn beide Futures abgeschlossen sind
                                List<LeagueEntryDTO> leagueEntries = leagueEntriesFuture.join(); // Extrahiert Liga-Ergebnisse (blockierend aber sicher)
                                List<MatchV5Dto> matchHistory = matchHistoryFuture.join(); // Extrahiert Match-Ergebnisse (blockierend aber sicher)

                                playerLpRecordService.calculateAndSetLpChangesForMatches(summoner, matchHistory); // Business-Logik: Berechnet LP-Änderungen für jedes Match basierend auf gespeicherten LP-Records

                                Map<String, Long> championPlayCounts = getChampionPlayCounts(matchHistory, summoner.getPuuid()); // Business-Logik: Berechnet Champion-Statistiken

                                return new SummonerProfileData(summoner, leagueEntries, matchHistory, suggestionDTO, championPlayCounts, iconUrl); // Erstellt vollständiges Profildaten-Objekt
                            });
                })
                .exceptionally(ex -> { // Exception-Handler für gesamte Profildaten-Aggregation
                    logger.error("Error building summoner profile data for {}#{}: {}", gameName, tagLine, ex.getMessage(), ex); // Loggt Fehler
                    return new SummonerProfileData("An error occurred while fetching summoner profile data: " + ex.getMessage()); // Gibt Fehler-Profildaten zurück
                });
    }


    public CompletableFuture<Summoner> getSummonerViaRso(String bearerToken) { // Öffentliche Methode zum Abrufen von Summoner-Daten via RSO (Riot Sign-On) Bearer-Token (asynchron)
        if (!StringUtils.hasText(bearerToken)) { // Validierung: Prüft ob Bearer-Token vorhanden ist
            logger.error("Error: bearerToken is empty for RSO summoner request."); // Loggt Fehler wenn Token fehlt
            return CompletableFuture.completedFuture(null); // Gibt sofort abgeschlossenes Future mit null zurück
        }
        return riotApiClient.getSummonerMeWithBearer(bearerToken) // Asynchroner API-Aufruf: Holt Summoner-Daten via RSO-Token (OAuth-Authentifizierung)
                .exceptionally(ex -> { // Exception-Handler für RSO-Aufruf
                    logger.error("Error fetching RSO summoner via /me: {}", ex.getMessage(), ex); // Loggt Fehler bei RSO-Authentifizierung
                    return null; // Gibt null bei Fehler zurück
                });
    }

    private static String maskPuuid(String puuid) { // Private Utility-Methode zum Maskieren von PUUIDs für Logging (Datenschutz)
        if (puuid == null) return "(null)"; // Gibt "(null)" für null-Werte zurück
        int len = puuid.length(); // Ermittelt Länge der PUUID
        if (len <= 10) return "***"; // Gibt "***" für sehr kurze PUUIDs zurück (Vollmaskierung)
        return puuid.substring(0, 6) + "..." + puuid.substring(len - 4); // Maskiert PUUID: Zeigt erste 6 und letzte 4 Zeichen (z.B. "abc123...xyz9")
    }
} // Ende der RiotApiService-Klasse
