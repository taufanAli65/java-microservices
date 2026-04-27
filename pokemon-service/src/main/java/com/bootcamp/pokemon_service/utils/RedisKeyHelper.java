package com.bootcamp.pokemon_service.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyHelper {
    private final String cachePrefix;

    public RedisKeyHelper (
            @Value("${spring.cache.prefix}")
            String cachePrefix
    ) {
        this.cachePrefix = cachePrefix;
    }

    public String generateKey(String cacheName, String key) {
        return String.format("%s:%s:%s", cachePrefix, cacheName, key);
    }
}
