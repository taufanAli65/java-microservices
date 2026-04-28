package com.bootcamp.trade_service.consumer;

import com.bootcamp.trade_service.dto.message.TradeResultMessage;
import com.bootcamp.trade_service.dto.message.TradeStatusRetryMessage;
import com.bootcamp.trade_service.entity.TradeStatus;
import com.bootcamp.trade_service.producer.TradeProducer;
import com.bootcamp.trade_service.service.TradeService;
import com.bootcamp.trade_service.support.TradeKafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeConsumer {
    private final TradeService tradeService;
    private final TradeProducer tradeProducer;

    @KafkaListener(
            id = "POKEMONTRADESUCCESS",
            topics = TradeKafkaTopics.POKEMON_TRADE_SUCCESS,
            containerFactory = "tradeResultKafkaListenerContainerFactory"
    )
    public void handleTradeSuccess(TradeResultMessage message) {
        updateTradeStatus(message, TradeStatus.SUCCESS);
    }

    @KafkaListener(
            id = "POKEMONTRADEFAILED",
            topics = TradeKafkaTopics.POKEMON_TRADE_FAILED,
            containerFactory = "tradeResultKafkaListenerContainerFactory"
    )
    public void handleTradeFailed(TradeResultMessage message) {
        updateTradeStatus(message, TradeStatus.FAILED);
    }

    @KafkaListener(
            id = "UPDATEPOKEMONTRADEFAILED",
            topics = TradeKafkaTopics.UPDATE_POKEMON_TRADE_FAILED,
            containerFactory = "tradeRetryKafkaListenerContainerFactory"
    )
    public void retryTradeStatusUpdate(TradeStatusRetryMessage message) {
        tradeService.updateTradeStatus(message.getTradeId(), message.getTargetStatus());
    }

    @KafkaListener(
            id = "UPDATEPOKEMONTRADEFAILED_DLT",
            topics = TradeKafkaTopics.UPDATE_POKEMON_TRADE_FAILED_DLT,
            containerFactory = "tradeRetryKafkaListenerContainerFactory"
    )
    public void handleRetryDeadLetter(TradeStatusRetryMessage message) {
        log.error("Trade status retry exhausted and moved to DLT: {}", message);
    }

    private void updateTradeStatus(TradeResultMessage message, TradeStatus targetStatus) {
        try {
            tradeService.updateTradeStatus(message.getTradeId(), targetStatus);
        } catch (RuntimeException exception) {
            log.error(
                    "Failed to update trade status for tradeId={} to {}. Sending retry event.",
                    message.getTradeId(),
                    targetStatus,
                    exception
            );
            tradeProducer.sendTradeStatusRetry(
                    new TradeStatusRetryMessage(
                            message.getTradeId(),
                            message.getReceiverId(),
                            message.getReceiverPokemonId(),
                            message.getRequesterId(),
                            message.getRequesterPokemonId(),
                            targetStatus
                    )
            );
        }
    }
}
