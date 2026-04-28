package com.bootcamp.pokemon_service.service;

import com.bootcamp.pokemon_service.dto.message.TradeCreatedMessage;

public interface TradeProcessingService {
    void processTradeCreated(TradeCreatedMessage message);
}
