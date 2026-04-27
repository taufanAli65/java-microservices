package com.bootcamp.pokemon_service.consumer;

import com.bootcamp.pokemon_service.dto.message.ProductViewMessage;
import com.bootcamp.pokemon_service.dto.response.ResGetProductDto;
import com.bootcamp.pokemon_service.utils.RedisKeyHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductConsumer {
    private final RedisKeyHelper redisKeyHelper;
    private final StringRedisTemplate stringRedisTemplate;

    @KafkaListener(
            id = "PRODUCT_VIEW",
            topics = "PRODUCT_VIEW",
            containerFactory = "",
            autoStartup = "true"
    )
    public void processProductView(ProductViewMessage message) {
        String trendingKey = redisKeyHelper.generateKey("pokemon:trending", "global");
        stringRedisTemplate.opsForZSet().incrementScore(trendingKey, message.getPokemonId(), 1);

        if (message.getUserId() != null) {
            String historyKey = redisKeyHelper.generateKey("user:history", message.getUserId().toString());

            stringRedisTemplate.opsForList().remove(historyKey, 0, message.getPokemonId());
            stringRedisTemplate.opsForList().leftPush(historyKey, message.getPokemonId());

            stringRedisTemplate.opsForList().trim(historyKey, 0, 4);
            log.info("Done processing pokemon trend and user history");
        }
    }
}
