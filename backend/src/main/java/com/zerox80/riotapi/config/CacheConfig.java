// Package declaration - defines that this configuration class belongs to the config package
package com.zerox80.riotapi.config;

// Import for Caffeine Cache Builder - high-performance Java caching library
import com.github.benmanes.caffeine.cache.Caffeine;
// Import for Spring's CacheManager interface
import org.springframework.cache.CacheManager;
// Import for Spring's Caffeine Cache Manager implementation
import org.springframework.cache.caffeine.CaffeineCacheManager;
// Import for @Bean annotation - used to declare Spring beans
import org.springframework.context.annotation.Bean;
// Import for @Configuration annotation - marks this class as a configuration source
import org.springframework.context.annotation.Configuration;
// Import for @Primary annotation - marks this bean as the default CacheManager
import org.springframework.context.annotation.Primary;

// Import for time units (HOURS, MINUTES, DAYS)
import java.util.concurrent.TimeUnit;


// @Configuration - marks this class as a source of bean definitions
@Configuration
/**
 * CacheConfig configures all caches used in the application.
 * Uses Caffeine as the caching provider with different TTL and size settings for each cache.
 * Caches include Riot API data (accounts, summoners, matches) and Data Dragon static data (champions, items, runes).
 */
public class CacheConfig {

    /**
     * Creates and configures the main cache manager bean.
     * Registers multiple named caches with specific expiration and size settings.
     * All caches use async mode for better concurrency and non-blocking operations.
     *
     * @return Configured CaffeineCacheManager with all application caches
     */
    @Primary
    @Bean("caffeineCacheManager")
    public CaffeineCacheManager cacheManager() {
        // Create new instance of CaffeineCacheManager (Spring wrapper for Caffeine)
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // Enable asynchronous cache mode for better concurrency
        // Prevents blocking on cache operations
        cacheManager.setAsyncCacheMode(true);

        // Register cache for "accounts" (Riot Account data: puuid, gameName, tagLine)
        cacheManager.registerCustomCache("accounts",
            Caffeine.newBuilder()
                // Entries expire 12 hours after write - account data rarely changes
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 2000 account entries - LRU eviction when limit reached
                .maximumSize(2000)
                .buildAsync());

        // Register cache for "summoners" (Summoner data: level, name, icon)
        cacheManager.registerCustomCache("summoners",
            Caffeine.newBuilder()
                // Entries expire after 12 hours - summoner data (level, name) changes infrequently
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 2000 summoners can be cached
                .maximumSize(2000)
                .buildAsync());

        // Register cache for "leagueEntries" (Ranked data: rank, division, LP)
        cacheManager.registerCustomCache("leagueEntries",
            Caffeine.newBuilder()
                // Entries expire after 10 minutes - ranked data changes frequently (after each game)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // Maximum 2000 league entries
                .maximumSize(2000)
                .buildAsync());

        // Register cache for "matchIds" (Lists of match IDs per player)
        cacheManager.registerCustomCache("matchIds",
            Caffeine.newBuilder()
                // Entries expire after 5 minutes - match IDs change frequently (new games)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // Maximum 5000 match ID lists (more than summoners due to pagination)
                .maximumSize(5000)
                .buildAsync());

        // Register cache for "matchDetails" (Detailed match data)
        cacheManager.registerCustomCache("matchDetails",
            Caffeine.newBuilder()
                // Entries expire after 7 days - match details NEVER change (historical data)
                .expireAfterWrite(7, TimeUnit.DAYS)
                // Maximum 10000 match details (largest cache, lots of data)
                .maximumSize(10000)
                .buildAsync());

        // Register cache for "matchHistory" (Aggregated match history per player)
        cacheManager.registerCustomCache("matchHistory",
            Caffeine.newBuilder()
                // Entries expire after 5 minutes - match history changes with new games
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // Maximum 2000 match histories
                .maximumSize(2000)
                .buildAsync());

        // Static data (Data Dragon): cache generously, these change only per patch

        // Register cache for "ddragonVersions" (Available LoL patch versions)
        cacheManager.registerCustomCache("ddragonVersions",
            Caffeine.newBuilder()
                // Entries expire after 12 hours - versions change only on new patches (~2 weeks)
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 10 versions (very small data)
                .maximumSize(10)
                .buildAsync());

        // Register cache for "ddragonChampionList" (List of all champions)
        cacheManager.registerCustomCache("ddragonChampionList",
            Caffeine.newBuilder()
                // Entries expire after 12 hours - champion list changes only on new champion releases (rare)
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 2 entries (usually only one language/version active)
                .maximumSize(2)
                .buildAsync());

        // Register cache for "ddragonChampionDetail" (Detailed champion data)
        cacheManager.registerCustomCache("ddragonChampionDetail",
            Caffeine.newBuilder()
                // Entries expire after 12 hours - champion details change only on balance patches
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 300 champions (about ~160 champions currently exist)
                .maximumSize(300)
                .buildAsync());

        // Register cache for "ddragonItems" (Item data: costs, stats, recipes)
        cacheManager.registerCustomCache("ddragonItems",
            Caffeine.newBuilder()
                // Entries expire after 12 hours - items change only on patches
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 2 entries (complete item list per version)
                .maximumSize(2)
                .buildAsync());

        // Register cache for "ddragonRunes" (Rune data: keystones, etc.)
        cacheManager.registerCustomCache("ddragonRunes",
            Caffeine.newBuilder()
                // Entries expire after 12 hours - runes change only on major patches
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 2 entries (complete rune list)
                .maximumSize(2)
                .buildAsync());

        // Register cache for "ddragonSummonerSpells" (Summoner spells: Flash, Ignite, etc.)
        cacheManager.registerCustomCache("ddragonSummonerSpells",
            Caffeine.newBuilder()
                // Entries expire after 12 hours - summoner spells change very rarely
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 2 entries
                .maximumSize(2)
                .buildAsync());

        // Register cache for "ddragonImageBases" (Base URLs for champion/item images)
        cacheManager.registerCustomCache("ddragonImageBases",
            Caffeine.newBuilder()
                // Entries expire after 12 hours - image URLs change only with new DDragon versions
                .expireAfterWrite(12, TimeUnit.HOURS)
                // Maximum 5 entries (different asset types)
                .maximumSize(5)
                .buildAsync());

        // Return fully configured cache manager for Spring bean registration
        return cacheManager;
    }
}
