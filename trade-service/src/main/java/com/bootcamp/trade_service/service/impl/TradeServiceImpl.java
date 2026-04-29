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
import com.bootcamp.trade_service.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TradeServiceImpl implements TradeService {
    private final TradeRepository tradeRepository;
    private final PokemonClient pokemonClient;
    private final TradeProducer tradeProducer;

    public TradeServiceImpl(
            TradeRepository tradeRepository,
            PokemonClient pokemonClient,
            TradeProducer tradeProducer
    ) {
        this.tradeRepository = tradeRepository;
        this.pokemonClient = pokemonClient;
        this.tradeProducer = tradeProducer;
    }

    @Override
    @Transactional
    public void tradePokemon(Long requesterId, Long receiverId, String requesterPokemonId, String receiverPokemonId) {
        ensurePokemonOwnership(
                pokemonClient.getRequesterPokemon(requesterId).getBody(),
                requesterPokemonId,
                "Pokemon requester tidak ditemukan"
        );
        ensurePokemonOwnership(
                pokemonClient.getReceiverPokemon(receiverId).getBody(),
                receiverPokemonId,
                "Pokemon receiver tidak ditemukan"
        );
        ensurePokemonNotInPendingTrade(
                requesterId,
                requesterPokemonId,
                "Pokemon requester sedang dalam trade pending"
        );
        ensurePokemonNotInPendingTrade(
                receiverId,
                receiverPokemonId,
                "Pokemon receiver sedang dalam trade pending"
        );

        TradeHistoryEntity trade = new TradeHistoryEntity();
        trade.setRequesterId(requesterId);
        trade.setRequesterPokemonId(requesterPokemonId);
        trade.setReceiverId(receiverId);
        trade.setReceiverPokemonId(receiverPokemonId);
        trade.setStatus(TradeStatus.PENDING);

        TradeHistoryEntity savedTrade = tradeRepository.save(trade);
        tradeProducer.sendTradeCreated(
                new TradeCreatedMessage(
                        savedTrade.getId(),
                        receiverId,
                        receiverPokemonId,
                        requesterId,
                        requesterPokemonId
                )
        );
    }

    @Override
    @Transactional
    public void updateTradeStatus(Long tradeId, TradeStatus status) {
        TradeHistoryEntity trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new DataNotFoundException("Trade tidak ditemukan"));
        trade.setStatus(status);
        tradeRepository.save(trade);
    }

    private void ensurePokemonOwnership(
            BaseResponse<List<ResMyPokemonDto>> response,
            String expectedPokemonId,
            String notFoundMessage
    ) {
        List<ResMyPokemonDto> pokemons = response == null ? List.of() : response.getData();
        boolean isOwned = pokemons != null && pokemons.stream()
                .filter(Objects::nonNull)
                .map(ResMyPokemonDto::getId)
                .anyMatch(expectedPokemonId::equals);

        if (!isOwned) {
            throw new DataNotFoundException(notFoundMessage);
        }
    }

        private void ensurePokemonNotInPendingTrade(Long ownerId, String pokemonId, String errorMessage) {
                boolean isInPendingTrade = tradeRepository.existsPendingTradeForOwnerPokemon(
                                TradeStatus.PENDING,
                                ownerId,
                                pokemonId,
                                ownerId,
                                pokemonId
                );

                if (isInPendingTrade) {
                        throw new BadRequestException(errorMessage);
                }
        }
}
