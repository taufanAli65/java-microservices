package com.bootcamp.pokemon_service.config;

import com.bootcamp.pokemon_service.dto.response.ResGetProductDto;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.List;

@Slf4j
@Configuration
public class RedisConfig {
    @Value("${spring.cache.host:localhost}")
    private String redisHost;

        @Value("${spring.cache.port:6379}")
    private int redisPort;

    @Value(("${spring.cache.isCredential:false}"))
    private boolean isCredential;

        @Value("${spring.cache.prefix}")
    private String cachePrefix;

    @Value("${spring.cache.default-ttl:60}")
    private Long defaultTimeToLiveMinutes;

    @Bean
    public RedissonClient redisClient() {
        Config config = new Config();
                config
                                .useSingleServer()
                                .setAddress("redis://" + redisHost + ":" + redisPort)
                                .setConnectTimeout(15000)
                                .setTimeout(15000);

        return Redisson.create(config);
    }

    @Bean
    public JedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration(redisHost, redisPort);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxIdle(80);
        poolConfig.setMinIdle(20);
        poolConfig.setTestOnBorrow(true);

        JedisClientConfiguration jedisClientConfig = JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofMillis(15000))
                .readTimeout(Duration.ofMillis(15000))
                .usePooling().poolConfig(poolConfig)
                .build();

        return new JedisConnectionFactory(conf, jedisClientConfig);
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
            ObjectMapper redisObjectMapper
    ) {
        return builder -> {
            RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(defaultTimeToLiveMinutes))
                    .computePrefixWith(cacheName -> cachePrefix + ":" + cacheName + ":")
                    .disableCachingNullValues();

            builder
                    .withCacheConfiguration(
                            "pokemon:detail",
                            createCacheConfig(
                                    defaultConfig,
                                    redisObjectMapper,
                                    defaultTimeToLiveMinutes,
                                    ResGetProductDto.class
                            )
                    );
        };
    }

    private <T> RedisCacheConfiguration createCacheConfig (
            RedisCacheConfiguration baseConfig,
            ObjectMapper mapper,
            Long ttlMinutes,
            Class<T> type
    ) {
        return baseConfig
                .entryTtl(Duration.ofMinutes(ttlMinutes))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new Jackson2JsonRedisSerializer<>(mapper, type)
                        )
                );
    }

    private <T> RedisCacheConfiguration createListCacheConfiguration (
            RedisCacheConfiguration baseConfig,
            ObjectMapper mapper,
            Long ttlMinutes,
            Class<T> elementType
    ) {
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, elementType);
        return baseConfig
                .entryTtl(Duration.ofMinutes(ttlMinutes))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new Jackson2JsonRedisSerializer<>(mapper, type)
                        )
                );
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            JedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}
