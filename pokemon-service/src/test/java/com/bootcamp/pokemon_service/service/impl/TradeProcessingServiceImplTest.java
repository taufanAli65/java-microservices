package com.bootcamp.pokemon_service.service.impl;

import com.bootcamp.pokemon_service.dto.message.TradeCreatedMessage;
import com.bootcamp.pokemon_service.dto.message.TradeResultMessage;
import com.bootcamp.pokemon_service.entity.PokemonEntity;
import com.bootcamp.pokemon_service.entity.UserPokemonEntity;
import com.bootcamp.pokemon_service.producer.KafkaProducer;
import com.bootcamp.pokemon_service.repository.UserPokemonRepository;
import com.bootcamp.pokemon_service.support.TradeKafkaTopics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeProcessingServiceImplTest {
    @Mock
    private UserPokemonRepository userPokemonRepository;

    @Mock
    private KafkaProducer<Object> kafkaProducer;

    @InjectMocks
    private TradeProcessingServiceImpl tradeProcessingService;

    @Test
    void processTradeCreatedSwapsOwnershipAndPublishesSuccess() {
        TradeCreatedMessage message = new TradeCreatedMessage(10L, 2L, "receiver-card", 1L, "requester-card");
        UserPokemonEntity requesterPokemon = createOwnedPokemon(100L, 1L, "requester-card");
        UserPokemonEntity receiverPokemon = createOwnedPokemon(200L, 2L, "receiver-card");

        when(userPokemonRepository.findByUserIdAndPokemonId_Id(1L, "requester-card"))
                .thenReturn(Optional.of(requesterPokemon));
        when(userPokemonRepository.findByUserIdAndPokemonId_Id(2L, "receiver-card"))
                .thenReturn(Optional.of(receiverPokemon));
        when(userPokemonRepository.save(any(UserPokemonEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        tradeProcessingService.processTradeCreated(message);

        assertEquals(2L, requesterPokemon.getUserId());
        assertEquals(1L, receiverPokemon.getUserId());
        verify(userPokemonRepository, times(2)).save(any(UserPokemonEntity.class));

        ArgumentCaptor<TradeResultMessage> resultCaptor = ArgumentCaptor.forClass(TradeResultMessage.class);
        verify(kafkaProducer).sendMesage(eq(TradeKafkaTopics.POKEMON_TRADE_SUCCESS), resultCaptor.capture());
        assertEquals(10L, resultCaptor.getValue().getTradeId());
    }

    @Test
    void processTradeCreatedPublishesFailedWhenOwnershipDoesNotMatch() {
        TradeCreatedMessage message = new TradeCreatedMessage(10L, 2L, "receiver-card", 1L, "requester-card");

        when(userPokemonRepository.findByUserIdAndPokemonId_Id(1L, "requester-card"))
                .thenReturn(Optional.empty());

        tradeProcessingService.processTradeCreated(message);

        verify(userPokemonRepository, never()).save(any(UserPokemonEntity.class));
        verify(kafkaProducer).sendMesage(eq(TradeKafkaTopics.POKEMON_TRADE_FAILED), any(TradeResultMessage.class));
    }

    @Test
    void processTradeCreatedFailsOnSecondAttemptAfterOwnershipAlreadySwapped() {
        TradeCreatedMessage message = new TradeCreatedMessage(10L, 2L, "receiver-card", 1L, "requester-card");
        UserPokemonEntity requesterPokemon = createOwnedPokemon(100L, 1L, "requester-card");
        UserPokemonEntity receiverPokemon = createOwnedPokemon(200L, 2L, "receiver-card");

        when(userPokemonRepository.findByUserIdAndPokemonId_Id(anyLong(), eq("requester-card")))
                .thenAnswer(invocation -> requesterPokemon.getUserId().equals(invocation.getArgument(0))
                        ? Optional.of(requesterPokemon)
                        : Optional.empty());
        when(userPokemonRepository.findByUserIdAndPokemonId_Id(anyLong(), eq("receiver-card")))
                .thenAnswer(invocation -> receiverPokemon.getUserId().equals(invocation.getArgument(0))
                        ? Optional.of(receiverPokemon)
                        : Optional.empty());
        when(userPokemonRepository.save(any(UserPokemonEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        tradeProcessingService.processTradeCreated(message);
        tradeProcessingService.processTradeCreated(message);

        verify(kafkaProducer).sendMesage(eq(TradeKafkaTopics.POKEMON_TRADE_SUCCESS), any(TradeResultMessage.class));
        verify(kafkaProducer).sendMesage(eq(TradeKafkaTopics.POKEMON_TRADE_FAILED), any(TradeResultMessage.class));
    }

    private UserPokemonEntity createOwnedPokemon(Long userPokemonId, Long userId, String pokemonId) {
        PokemonEntity pokemon = new PokemonEntity();
        pokemon.setId(pokemonId);

        UserPokemonEntity entity = new UserPokemonEntity();
        entity.setId(userPokemonId);
        entity.setUserId(userId);
        entity.setPokemonId(pokemon);
        return entity;
    }
}
