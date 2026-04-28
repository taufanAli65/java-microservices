package com.bootcamp.trade_service.consumer;

import com.bootcamp.trade_service.dto.message.TradeResultMessage;
import com.bootcamp.trade_service.dto.message.TradeStatusRetryMessage;
import com.bootcamp.trade_service.entity.TradeStatus;
import com.bootcamp.trade_service.producer.TradeProducer;
import com.bootcamp.trade_service.service.TradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TradeConsumerTest {
    @Mock
    private TradeService tradeService;

    @Mock
    private TradeProducer tradeProducer;

    @InjectMocks
    private TradeConsumer tradeConsumer;

    @Test
    void handleTradeSuccessUpdatesTradeStatusToSuccess() {
        TradeResultMessage message = new TradeResultMessage(10L, 2L, "receiver-card", 1L, "requester-card");

        tradeConsumer.handleTradeSuccess(message);

        verify(tradeService).updateTradeStatus(10L, TradeStatus.SUCCESS);
    }

    @Test
    void handleTradeFailedUpdatesTradeStatusToFailed() {
        TradeResultMessage message = new TradeResultMessage(10L, 2L, "receiver-card", 1L, "requester-card");

        tradeConsumer.handleTradeFailed(message);

        verify(tradeService).updateTradeStatus(10L, TradeStatus.FAILED);
    }

    @Test
    void handleTradeSuccessPublishesRetryEventWhenStatusUpdateFails() {
        TradeResultMessage message = new TradeResultMessage(10L, 2L, "receiver-card", 1L, "requester-card");
        doThrow(new IllegalStateException("db error"))
                .when(tradeService)
                .updateTradeStatus(10L, TradeStatus.SUCCESS);

        tradeConsumer.handleTradeSuccess(message);

        ArgumentCaptor<TradeStatusRetryMessage> retryCaptor = ArgumentCaptor.forClass(TradeStatusRetryMessage.class);
        verify(tradeProducer).sendTradeStatusRetry(retryCaptor.capture());
        assertEquals(TradeStatus.SUCCESS, retryCaptor.getValue().getTargetStatus());
        assertEquals(10L, retryCaptor.getValue().getTradeId());
    }

    @Test
    void retryTradeStatusUpdateUsesRequestedTerminalStatus() {
        TradeStatusRetryMessage message = new TradeStatusRetryMessage(
                10L,
                2L,
                "receiver-card",
                1L,
                "requester-card",
                TradeStatus.FAILED
        );

        tradeConsumer.retryTradeStatusUpdate(message);

        verify(tradeService).updateTradeStatus(10L, TradeStatus.FAILED);
    }
}
