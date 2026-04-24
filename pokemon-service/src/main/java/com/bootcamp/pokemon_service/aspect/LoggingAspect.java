package com.bootcamp.pokemon_service.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final ObjectMapper mapper;
    private static final Logger DATABASE_LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.bootcamp.pokemon_service.controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        log.info(
                "Incoming Request -> method [{}], path: {}, class: {}, method: {}, start: {}ms",
                method, uri, className, methodName, start
        );

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;

            log.info("Outgoing response -> [{}] Success in {}ms", response.getStatus(), duration);

            if (result instanceof ResponseEntity<?> responseEntity) {
                Object body = responseEntity.getBody();
                String responseBodyJson = mapper.writeValueAsString(body);
                log.info("Response Body (Data Only): {}", responseBodyJson);
            }
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            log.error("Outgoing error -> {} {} | Failed: {} | time: {}ms", method, uri, e.getMessage(), duration);
            throw e;
        }
    }

    @Around("execution(* com.bootcamp.pokemon_service.repository.*.*(..))")
    public Object logDatabaseQuery(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();

        DATABASE_LOGGER.info("Executing database query: {}", methodName);

        try {
            Object result = joinPoint.proceed();
            long timeTaken = System.currentTimeMillis() - startTime;
            DATABASE_LOGGER.info("Query {} completed in {} ms", methodName, timeTaken);
            return result;
        } catch (Exception e) {
            DATABASE_LOGGER.error("Query {} failed: {}", methodName, e.getMessage());
            throw e;
        }
    }

    @Around("execution(* com.bootcamp.pokemon_service.rest.PokemonClient.*(..))")
    public Object logFeignCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String method = joinPoint.getSignature().toShortString();
        log.info("Outgoing Feign request -> {}", method);
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            log.info("Incoming Feign response -> {} completed in {}ms", method, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            log.error("Feign call failed -> {} in {}ms, reason: {}", method, executionTime, e.getMessage());
            throw e;
        }
    }

    @AfterThrowing(pointcut = "execution(* com.bootcamp.pokemon_service..*.*(..))", throwing = "errorObj")
    public void logThrowException(JoinPoint joinPoint, Exception errorObj) {
        log.error("Error(Message Only) -> {}", errorObj.getMessage());
    }
}
