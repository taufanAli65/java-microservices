package com.bootcamp.pokemon_service.consumer;

import com.bootcamp.pokemon_service.dto.message.TradeCreatedMessage;
import com.bootcamp.pokemon_service.service.TradeProcessingService;
import com.bootcamp.pokemon_service.support.TradeKafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeConsumer {
    private final TradeProcessingService tradeProcessingService;

    @KafkaListener(
            id = "TRADECREATED",
            topics = TradeKafkaTopics.TRADE_CREATED,
            containerFactory = "tradeCreatedKafkaListenerContainerFactory"
    )
    public void processTradeCreated(TradeCreatedMessage message) {
        tradeProcessingService.processTradeCreated(message);
    }
}
