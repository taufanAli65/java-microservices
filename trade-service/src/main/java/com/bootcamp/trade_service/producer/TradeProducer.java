package com.bootcamp.trade_service.producer;

import com.bootcamp.trade_service.dto.message.TradeCreatedMessage;
import com.bootcamp.trade_service.dto.message.TradeStatusRetryMessage;
import com.bootcamp.trade_service.support.TradeKafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTradeCreated(TradeCreatedMessage message) {
        kafkaTemplate.send(TradeKafkaTopics.TRADE_CREATED, message.getTradeId().toString(), message);
    }

    public void sendTradeStatusRetry(TradeStatusRetryMessage message) {
        kafkaTemplate.send(TradeKafkaTopics.UPDATE_POKEMON_TRADE_FAILED, message.getTradeId().toString(), message);
    }
}
