package com.bootcamp.trade_service.service;

import com.bootcamp.trade_service.entity.TradeStatus;

public interface TradeService {
    void tradePokemon(Long requesterId, Long receiverId, String requesterPokemonId, String receiverPokemonId);

    void updateTradeStatus(Long tradeId, TradeStatus status);
}
