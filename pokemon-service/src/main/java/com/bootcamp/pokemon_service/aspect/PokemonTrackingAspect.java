package com.bootcamp.pokemon_service.aspect;

import com.bootcamp.pokemon_service.dto.message.ProductViewMessage;
import com.bootcamp.pokemon_service.dto.response.ResGetProductDto;
import com.bootcamp.pokemon_service.producer.KafkaProducer;
import com.bootcamp.pokemon_service.utils.DataHelper;
import com.bootcamp.pokemon_service.utils.RedisKeyHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
@Order(1)
public class PokemonTrackingAspect {
//    private final RedisKeyHelper redisKeyHelper;
//    private final StringRedisTemplate stringRedisTemplate;
    private final KafkaProducer<ProductViewMessage> kafkaProducer;

    @AfterReturning(
            pointcut = "execution(* com.bootcamp.pokemon_service.service.impl.ProductServiceImpl.getProductById(..))",
            returning = "result"

    )
    public void trackTrending(JoinPoint joinPoint, Object result) {
        if(result instanceof ResGetProductDto) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            Integer userId = null;

            if(attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String userIdHeader = request.getHeader("X-Authenticated-User-Id");
                if(userIdHeader != null) {
                    userId = Integer.parseInt(userIdHeader);
                }
            }

            ProductViewMessage message = new ProductViewMessage(
                    userId,
                    ((ResGetProductDto) result).getId(),
                    DataHelper.now()
            );

            kafkaProducer.sendMesage("PRODUCT_VIEW", message);
        }

//        String trendingKey = redisKeyHelper.generateKey("pokemon:trending", "global");
//        stringRedisTemplate.opsForZSet().incrementScore(trendingKey, ((ResGetProductDto) result).getId(), 1);
    }
}
