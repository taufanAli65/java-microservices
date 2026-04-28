package com.bootcamp.trade_service.config;

import com.bootcamp.trade_service.dto.message.TradeStatusRetryMessage;
import com.bootcamp.trade_service.entity.TradeStatus;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaConsumerConfigTest {

    @Test
    void tradeRetryErrorHandlerDeadLettersAfterConfiguredAttempts() {
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        when(kafkaTemplate.isTransactional()).thenReturn(false);
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(CompletableFuture.completedFuture(null));

        KafkaConsumerConfig config = new KafkaConsumerConfig();
        DefaultErrorHandler errorHandler = config.tradeRetryErrorHandler(kafkaTemplate);

        TradeStatusRetryMessage payload = new TradeStatusRetryMessage(
                77L,
                2L,
                "receiver-card",
                1L,
                "requester-card",
                TradeStatus.SUCCESS
        );
        ConsumerRecord<String, TradeStatusRetryMessage> record =
                new ConsumerRecord<>("UPDATEPOKEMONTRADEFAILED", 0, 0L, "77", payload);

        MessageListenerContainer container = mock(MessageListenerContainer.class);

        errorHandler.handleOne(new IllegalStateException("boom"), record, null, container);
        errorHandler.handleOne(new IllegalStateException("boom"), record, null, container);
        errorHandler.handleOne(new IllegalStateException("boom"), record, null, container);

        ArgumentCaptor<ProducerRecord<String, Object>> producerRecordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate, times(1)).send(producerRecordCaptor.capture());
        assertEquals("UPDATEPOKEMONTRADEFAILED.DLT", producerRecordCaptor.getValue().topic());
    }
}
