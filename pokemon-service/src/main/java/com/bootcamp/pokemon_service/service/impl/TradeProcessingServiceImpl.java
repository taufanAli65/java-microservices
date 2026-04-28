package com.bootcamp.pokemon_service.service.impl;

import com.bootcamp.pokemon_service.dto.message.TradeCreatedMessage;
import com.bootcamp.pokemon_service.dto.message.TradeResultMessage;
import com.bootcamp.pokemon_service.entity.UserPokemonEntity;
import com.bootcamp.pokemon_service.producer.KafkaProducer;
import com.bootcamp.pokemon_service.repository.UserPokemonRepository;
import com.bootcamp.pokemon_service.service.TradeProcessingService;
import com.bootcamp.pokemon_service.support.TradeKafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeProcessingServiceImpl implements TradeProcessingService {
    private final UserPokemonRepository userPokemonRepository;
    private final KafkaProducer<Object> kafkaProducer;

    @Override
    @Transactional
    public void processTradeCreated(TradeCreatedMessage message) {
        Optional<UserPokemonEntity> requesterPokemon = userPokemonRepository.findByUserIdAndPokemonId_Id(
                message.getRequesterId(),
                message.getRequesterPokemonId()
        );
        Optional<UserPokemonEntity> receiverPokemon = userPokemonRepository.findByUserIdAndPokemonId_Id(
                message.getReceiverId(),
                message.getReceiverPokemonId()
        );

        if (requesterPokemon.isEmpty() || receiverPokemon.isEmpty()) {
            publishTradeFailed(message);
            return;
        }

        UserPokemonEntity requesterEntity = requesterPokemon.get();
        UserPokemonEntity receiverEntity = receiverPokemon.get();

        requesterEntity.setUserId(message.getReceiverId());
        receiverEntity.setUserId(message.getRequesterId());
        userPokemonRepository.save(requesterEntity);
        userPokemonRepository.save(receiverEntity);

        kafkaProducer.sendMesage(
                TradeKafkaTopics.POKEMON_TRADE_SUCCESS,
                new TradeResultMessage(
                        message.getTradeId(),
                        message.getReceiverId(),
                        message.getReceiverPokemonId(),
                        message.getRequesterId(),
                        message.getRequesterPokemonId()
                )
        );
    }

    private void publishTradeFailed(TradeCreatedMessage message) {
        log.info("Trade cannot be completed because pokemon ownership no longer matches request. tradeId={}", message.getTradeId());
        kafkaProducer.sendMesage(
                TradeKafkaTopics.POKEMON_TRADE_FAILED,
                new TradeResultMessage(
                        message.getTradeId(),
                        message.getReceiverId(),
                        message.getReceiverPokemonId(),
                        message.getRequesterId(),
                        message.getRequesterPokemonId()
                )
        );
    }
}
