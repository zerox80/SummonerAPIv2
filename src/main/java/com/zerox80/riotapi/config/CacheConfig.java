package com.zerox80.riotapi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for application caching using Caffeine cache.
 * 
 * <p>This configuration sets up multiple cache instances with different expiration policies
 * and size limits optimized for the specific data types used in the SummonerAPI. It uses
 * Caffeine as the underlying cache implementation for high-performance in-memory caching.</p>
 * 
 * <p>Cache configurations:</p>
 * <ul>
 *   <li><strong>Accounts/Summoners</strong>: 12 hours expiration, 2000 entries max</li>
 *   <li><strong>League Entries</strong>: 10 minutes expiration, 2000 entries max</li>
 *   <li><strong>Match IDs</strong>: 5 minutes expiration, 5000 entries max</li>
 *   <li><strong>Match Details</strong>: 7 days expiration, 10000 entries max</li>
 *   <li><strong>Data Dragon</strong>: 12 hours expiration, various size limits</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Configuration
public class CacheConfig {

    /**
     * Creates and configures the primary cache manager using Caffeine.
     * 
     * <p>This cache manager is configured with multiple custom caches, each optimized
     * for specific data types with appropriate expiration policies and size limits.
     * All caches are configured in async mode for better performance.</p>
     * 
     * @return A configured CaffeineCacheManager instance with all custom caches registered
     */
    @Primary
    @Bean("caffeineCacheManager")
    public CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setAsyncCacheMode(true);

        cacheManager.registerCustomCache("accounts",
            Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .maximumSize(2000)
                .buildAsync());
        cacheManager.registerCustomCache("summoners",
            Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .maximumSize(2000)
                .buildAsync());
        cacheManager.registerCustomCache("leagueEntries",
            Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(2000)
                .buildAsync());
        cacheManager.registerCustomCache("matchIds",
            Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(5000)
                .buildAsync());
        cacheManager.registerCustomCache("matchDetails",
            Caffeine.newBuilder()
                .expireAfterWrite(7, TimeUnit.DAYS)
                .maximumSize(10000)
                .buildAsync());
        cacheManager.registerCustomCache("matchHistory",
            Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(2000)
                .buildAsync());
        // Static data (Data Dragon): cache generously, these change only per patch
        cacheManager.registerCustomCache("ddragonVersions",
            Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .maximumSize(10)
                .buildAsync());
        cacheManager.registerCustomCache("ddragonChampionList",
            Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .maximumSize(2)
                .buildAsync());
        cacheManager.registerCustomCache("ddragonChampionDetail",
            Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .maximumSize(300)
                .buildAsync());
        cacheManager.registerCustomCache("ddragonItems",
            Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .maximumSize(2)
                .buildAsync());
        cacheManager.registerCustomCache("ddragonRunes",
            Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .maximumSize(2)
                .buildAsync());
        cacheManager.registerCustomCache("ddragonSummonerSpells",
            Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .maximumSize(2)
                .buildAsync());
        cacheManager.registerCustomCache("ddragonImageBases",
            Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .maximumSize(5)
                .buildAsync());
        return cacheManager;
    }
}