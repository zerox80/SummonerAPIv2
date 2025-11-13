// Package-Deklaration: Definiert dass diese Konfigurationsklasse zum Config-Package gehört
package com.zerox80.riotapi.config;

// Import für Caffeine Cache Builder - High-Performance Java Caching Library
import com.github.benmanes.caffeine.cache.Caffeine;
// Import für Spring's Cache-Manager Interface
import org.springframework.cache.CacheManager;
// Import für Spring's Caffeine Cache Manager Implementation
import org.springframework.cache.caffeine.CaffeineCacheManager;
// Import für @Bean Annotation um Spring-Beans zu deklarieren
import org.springframework.context.annotation.Bean;
// Import für @Configuration um diese Klasse als Konfigurationsquelle zu markieren
import org.springframework.context.annotation.Configuration;
// Import für @Primary um diese Bean als Standard-CacheManager zu markieren
import org.springframework.context.annotation.Primary;

// Import für Zeit-Einheiten (HOURS, MINUTES, DAYS)
import java.util.concurrent.TimeUnit;


// @Configuration: Markiert diese Klasse als Quelle von Bean-Definitionen
@Configuration
// Öffentliche Klasse: Konfiguration aller Caches in der Anwendung
public class CacheConfig {

    // Javadoc würde beschreiben: Erstellt und konfiguriert den Haupt-Cache-Manager

    // @Primary: Markiert diese Bean als primäre Wahl wenn mehrere CacheManager existieren
    @Primary
    // @Bean: Registriert Rückgabewert als Bean im Spring Container
    // name = "caffeineCacheManager": Gibt der Bean einen expliziten Namen
    @Bean("caffeineCacheManager")
    // public: Methode ist öffentlich zugänglich
    // CaffeineCacheManager: Return-Type, Caffeine-basierter Cache Manager
    // cacheManager(): Methodenname, erstellt den Cache Manager
    public CaffeineCacheManager cacheManager() {
        // Erstellt neue Instanz von CaffeineCacheManager (Spring Wrapper um Caffeine)
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // Aktiviert asynchronen Cache-Modus für bessere Concurrency
        // Verhindert Blockierung bei Cache-Operationen
        cacheManager.setAsyncCacheMode(true);

        // Registriert Custom Cache für "accounts" (Riot Account Daten)
        // registerCustomCache(): Fügt einen benannten Cache mit spezifischer Konfiguration hinzu
        cacheManager.registerCustomCache("accounts",
            // Caffeine.newBuilder(): Startet Builder-Pattern für Cache-Konfiguration
            Caffeine.newBuilder()
                // Einträge verfallen 12 Stunden nach dem Schreiben
                // Account-Daten ändern sich selten
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximale Anzahl von Einträgen: 2000 Accounts
                // LRU-Eviction wenn Limit erreicht (Least Recently Used)
                .maximumSize(2000)
                // Baut asynchronen Cache (CompletableFuture-basiert)
                .buildAsync());
        // Registriert Cache für "summoners" (Summoner-Daten: Level, Name, Icon)
        cacheManager.registerCustomCache("summoners",
            // Neuer Caffeine Builder für Summoner-Cache
            Caffeine.newBuilder()
                // Einträge verfallen nach 12 Stunden
                // Summoner-Daten (Level, Name) ändern sich selten
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 2000 Summoner können gecached werden
                .maximumSize(2000)
                // Asynchroner Cache für non-blocking Operations
                .buildAsync());
        // Registriert Cache für "leagueEntries" (Ranked Daten: Rang, Division, LP)
        cacheManager.registerCustomCache("leagueEntries",
            // Neuer Caffeine Builder
            Caffeine.newBuilder()
                // Einträge verfallen nach 10 Minuten
                // Ranked-Daten ändern sich häufig (nach jedem Game)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // Maximum 2000 League-Einträge
                .maximumSize(2000)
                // Asynchroner Cache
                .buildAsync());
        // Registriert Cache für "matchIds" (Listen von Match-IDs pro Spieler)
        cacheManager.registerCustomCache("matchIds",
            // Neuer Caffeine Builder
            Caffeine.newBuilder()
                // Einträge verfallen nach 5 Minuten
                // Match-IDs ändern sich häufig (neue Games)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // Maximum 5000 Match-ID-Listen (mehr als Summoner wegen Pagination)
                .maximumSize(5000)
                // Asynchroner Cache
                .buildAsync());
        // Registriert Cache für "matchDetails" (Detaillierte Match-Daten)
        cacheManager.registerCustomCache("matchDetails",
            // Neuer Caffeine Builder
            Caffeine.newBuilder()
                // Einträge verfallen nach 7 Tagen
                // Match-Details ändern sich NIEMALS (historische Daten)
                .expireAfterWrite(7, TimeUnit.DAYS)
                // Maximum 10000 Match-Details (größter Cache, viele Daten)
                .maximumSize(10000)
                // Asynchroner Cache
                .buildAsync());
        // Registriert Cache für "matchHistory" (Aggregierte Match-History pro Spieler)
        cacheManager.registerCustomCache("matchHistory",
            // Neuer Caffeine Builder
            Caffeine.newBuilder()
                // Einträge verfallen nach 5 Minuten
                // Match-History ändert sich mit neuen Games
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // Maximum 2000 Match-Histories
                .maximumSize(2000)
                // Asynchroner Cache
                .buildAsync());
        // Kommentar: Statische Daten (Data Dragon): Generös cachen, ändern sich nur bei Patches
        // Static data (Data Dragon): cache generously, these change only per patch
        // Registriert Cache für "ddragonVersions" (Verfügbare LoL Patch-Versionen)
        cacheManager.registerCustomCache("ddragonVersions",
            // Neuer Caffeine Builder
            Caffeine.newBuilder()
                // Einträge verfallen nach 12 Stunden
                // Versionen ändern sich nur bei neuen Patches (~2 Wochen)
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 10 Versionen (sehr kleine Datenmenge)
                .maximumSize(10)
                // Asynchroner Cache
                .buildAsync());
        // Registriert Cache für "ddragonChampionList" (Liste aller Champions)
        cacheManager.registerCustomCache("ddragonChampionList",
            // Neuer Caffeine Builder
            Caffeine.newBuilder()
                // Einträge verfallen nach 12 Stunden
                // Champion-Liste ändert sich nur bei neuen Champion-Releases (selten)
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 2 Einträge (meist nur eine Sprache/Version aktiv)
                .maximumSize(2)
                // Asynchroner Cache
                .buildAsync());
        // Registriert Cache für "ddragonChampionDetail" (Detaillierte Champion-Daten)
        cacheManager.registerCustomCache("ddragonChampionDetail",
            // Neuer Caffeine Builder
            Caffeine.newBuilder()
                // Einträge verfallen nach 12 Stunden
                // Champion-Details ändern sich nur bei Balance-Patches
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 300 Champions (etwa ~160 Champions existieren aktuell)
                .maximumSize(300)
                // Asynchroner Cache
                .buildAsync());
        // Registriert Cache für "ddragonItems" (Item-Daten: Kosten, Stats, Rezepte)
        cacheManager.registerCustomCache("ddragonItems",
            // Neuer Caffeine Builder
            Caffeine.newBuilder()
                // Einträge verfallen nach 12 Stunden
                // Items ändern sich nur bei Patches
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 2 Einträge (komplette Item-Liste pro Version)
                .maximumSize(2)
                // Asynchroner Cache
                .buildAsync());
        // Registriert Cache für "ddragonRunes" (Runen-Daten: Keystones, etc.)
        cacheManager.registerCustomCache("ddragonRunes",
            // Neuer Caffeine Builder
            Caffeine.newBuilder()
                // Einträge verfallen nach 12 Stunden
                // Runen ändern sich nur bei Major-Patches
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 2 Einträge (komplette Runen-Liste)
                .maximumSize(2)
                // Asynchroner Cache
                .buildAsync());
        // Registriert Cache für "ddragonSummonerSpells" (Summoner Spells: Flash, Ignite, etc.)
        cacheManager.registerCustomCache("ddragonSummonerSpells",
            // Neuer Caffeine Builder
            Caffeine.newBuilder()
                // Einträge verfallen nach 12 Stunden
                // Summoner Spells ändern sich sehr selten
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 2 Einträge
                .maximumSize(2)
                // Asynchroner Cache
                .buildAsync());
        // Registriert Cache für "ddragonImageBases" (Basis-URLs für Champion/Item-Bilder)
        cacheManager.registerCustomCache("ddragonImageBases",
            // Neuer Caffeine Builder
            Caffeine.newBuilder()
                // Einträge verfallen nach 12 Stunden
                // Bild-URLs ändern sich nur mit neuen DDragon-Versionen
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 5 Einträge (verschiedene Asset-Typen)
                .maximumSize(5)
                // Asynchroner Cache
                .buildAsync());
        // Gibt den vollständig konfigurierten CacheManager zurück
        // Wird von Spring als Bean registriert
        return cacheManager;
    // Schließende geschweifte Klammer für Methode cacheManager
    }
// Schließende geschweifte Klammer für Klasse CacheConfig
}
