package com.enterprise.api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * High-performance read-optimization caching configuration using Caffeine.
 * Provides microsecond in-memory response times with automatic eviction and size bounds.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CUSTOMERS_CACHE = "customers";
    public static final String CUSTOMER_SUMMARIES_CACHE = "customer-summaries";
    public static final String ACCOUNTS_CACHE = "accounts";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                CUSTOMERS_CACHE,
                CUSTOMER_SUMMARIES_CACHE,
                ACCOUNTS_CACHE
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(5000)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .recordStats());
        return cacheManager;
    }
}
