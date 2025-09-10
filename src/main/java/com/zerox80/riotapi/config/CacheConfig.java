package com.zerox80.riotapi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

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
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .maximumSize(5000)
                .buildAsync());
        cacheManager.registerCustomCache("matchDetails",
            Caffeine.newBuilder()
                .expireAfterWrite(7, TimeUnit.DAYS)
                .maximumSize(10000)
                .buildAsync());
        cacheManager.registerCustomCache("matchHistory",
            Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
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
        return cacheManager;
    }
}