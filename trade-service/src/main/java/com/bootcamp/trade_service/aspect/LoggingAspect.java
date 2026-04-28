package com.bootcamp.trade_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.bootcamp.trade_service.producer.TradeProducer.send*(..))")
    public Object logProducer(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution("Kafka producer", joinPoint);
    }

    @Around(
            "execution(* com.bootcamp.trade_service.consumer.TradeConsumer.handleTradeSuccess(..)) || " +
            "execution(* com.bootcamp.trade_service.consumer.TradeConsumer.handleTradeFailed(..))"
    )
    public Object logTradeResultConsumer(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution("Kafka consumer", joinPoint);
    }

    @Around(
            "execution(* com.bootcamp.trade_service.consumer.TradeConsumer.retryTradeStatusUpdate(..)) || " +
            "execution(* com.bootcamp.trade_service.consumer.TradeConsumer.handleRetryDeadLetter(..))"
    )
    public Object logRetryConsumer(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution("Kafka retry", joinPoint);
    }

    private Object logExecution(String label, ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String method = joinPoint.getSignature().toShortString();
        log.info("{} start -> {} args={}", label, method, Arrays.toString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            log.info("{} success -> {} in {}ms", label, method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable throwable) {
            log.error(
                    "{} failed -> {} in {}ms, reason={}",
                    label,
                    method,
                    System.currentTimeMillis() - start,
                    throwable.getMessage()
            );
            throw throwable;
        }
    }
}
