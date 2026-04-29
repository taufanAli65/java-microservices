package com.bootcamp.trade_service.service.impl;

import com.bootcamp.trade_service.dto.message.TradeCreatedMessage;
import com.bootcamp.trade_service.dto.response.BaseResponse;
import com.bootcamp.trade_service.dto.response.ResMyPokemonDto;
import com.bootcamp.trade_service.entity.TradeHistoryEntity;
import com.bootcamp.trade_service.entity.TradeStatus;
import com.bootcamp.trade_service.exception.BadRequestException;
import com.bootcamp.trade_service.exception.DataNotFoundException;
import com.bootcamp.trade_service.producer.TradeProducer;
import com.bootcamp.trade_service.repository.TradeRepository;
import com.bootcamp.trade_service.rest.PokemonClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeServiceImplTest {
    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private PokemonClient pokemonClient;

    @Mock
    private TradeProducer tradeProducer;

    @InjectMocks
    private TradeServiceImpl tradeService;

    @Test
    void tradePokemonThrowsWhenRequesterPokemonIsMissing() {
        when(pokemonClient.getRequesterPokemon(1L))
                .thenReturn(ResponseEntity.ok(BaseResponse.success(List.of())));

        assertThrows(
                DataNotFoundException.class,
                () -> tradeService.tradePokemon(1L, 2L, "requester-card", "receiver-card")
        );

        verify(tradeRepository, never()).save(any());
        verify(tradeProducer, never()).sendTradeCreated(any());
    }

    @Test
    void tradePokemonThrowsWhenReceiverPokemonIsMissing() {
        when(pokemonClient.getRequesterPokemon(1L))
                .thenReturn(ResponseEntity.ok(BaseResponse.success(List.of(new ResMyPokemonDto("requester-card", "Pika", "Rare")))));
        when(pokemonClient.getReceiverPokemon(2L))
                .thenReturn(ResponseEntity.ok(BaseResponse.success(List.of())));

        assertThrows(
                DataNotFoundException.class,
                () -> tradeService.tradePokemon(1L, 2L, "requester-card", "receiver-card")
        );

        verify(tradeRepository, never()).save(any());
        verify(tradeProducer, never()).sendTradeCreated(any());
    }

    @Test
    void tradePokemonThrowsWhenRequesterPokemonHasPendingTrade() {
        when(pokemonClient.getRequesterPokemon(1L))
                .thenReturn(ResponseEntity.ok(BaseResponse.success(List.of(new ResMyPokemonDto("requester-card", "Pika", "Rare")))));
        when(pokemonClient.getReceiverPokemon(2L))
                .thenReturn(ResponseEntity.ok(BaseResponse.success(List.of(new ResMyPokemonDto("receiver-card", "Char", "Common")))));
        when(tradeRepository.existsPendingTradeForOwnerPokemon(
                eq(TradeStatus.PENDING),
                eq(1L),
                eq("requester-card"),
                eq(1L),
                eq("requester-card")
        )).thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> tradeService.tradePokemon(1L, 2L, "requester-card", "receiver-card")
        );

        verify(tradeRepository, never()).save(any());
        verify(tradeProducer, never()).sendTradeCreated(any());
    }

    @Test
    void tradePokemonThrowsWhenReceiverPokemonHasPendingTrade() {
        when(pokemonClient.getRequesterPokemon(1L))
                .thenReturn(ResponseEntity.ok(BaseResponse.success(List.of(new ResMyPokemonDto("requester-card", "Pika", "Rare")))));
        when(pokemonClient.getReceiverPokemon(2L))
                .thenReturn(ResponseEntity.ok(BaseResponse.success(List.of(new ResMyPokemonDto("receiver-card", "Char", "Common")))));
        when(tradeRepository.existsPendingTradeForOwnerPokemon(
                eq(TradeStatus.PENDING),
                eq(1L),
                eq("requester-card"),
                eq(1L),
                eq("requester-card")
        )).thenReturn(false);
        when(tradeRepository.existsPendingTradeForOwnerPokemon(
                eq(TradeStatus.PENDING),
                eq(2L),
                eq("receiver-card"),
                eq(2L),
                eq("receiver-card")
        )).thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> tradeService.tradePokemon(1L, 2L, "requester-card", "receiver-card")
        );

        verify(tradeRepository, never()).save(any());
        verify(tradeProducer, never()).sendTradeCreated(any());
    }

    @Test
    void tradePokemonCreatesPendingTradeAndPublishesTradeCreatedEvent() {
        when(pokemonClient.getRequesterPokemon(1L))
                .thenReturn(ResponseEntity.ok(BaseResponse.success(List.of(new ResMyPokemonDto("requester-card", "Pika", "Rare")))));
        when(pokemonClient.getReceiverPokemon(2L))
                .thenReturn(ResponseEntity.ok(BaseResponse.success(List.of(new ResMyPokemonDto("receiver-card", "Char", "Common")))));
        when(tradeRepository.save(any(TradeHistoryEntity.class))).thenAnswer(invocation -> {
            TradeHistoryEntity entity = invocation.getArgument(0);
            entity.setId(42L);
            return entity;
        });

        tradeService.tradePokemon(1L, 2L, "requester-card", "receiver-card");

        ArgumentCaptor<TradeHistoryEntity> entityCaptor = ArgumentCaptor.forClass(TradeHistoryEntity.class);
        verify(tradeRepository).save(entityCaptor.capture());
        assertEquals(TradeStatus.PENDING, entityCaptor.getValue().getStatus());
        assertEquals("requester-card", entityCaptor.getValue().getRequesterPokemonId());
        assertEquals("receiver-card", entityCaptor.getValue().getReceiverPokemonId());

        ArgumentCaptor<TradeCreatedMessage> messageCaptor = ArgumentCaptor.forClass(TradeCreatedMessage.class);
        verify(tradeProducer).sendTradeCreated(messageCaptor.capture());
        assertEquals(42L, messageCaptor.getValue().getTradeId());
        assertEquals(1L, messageCaptor.getValue().getRequesterId());
        assertEquals(2L, messageCaptor.getValue().getReceiverId());
    }

    @Test
    void updateTradeStatusPersistsRequestedStatus() {
        TradeHistoryEntity entity = new TradeHistoryEntity();
        entity.setId(7L);
        entity.setStatus(TradeStatus.PENDING);
        when(tradeRepository.findById(7L)).thenReturn(Optional.of(entity));

        tradeService.updateTradeStatus(7L, TradeStatus.SUCCESS);

        assertEquals(TradeStatus.SUCCESS, entity.getStatus());
        verify(tradeRepository).save(entity);
    }
}
