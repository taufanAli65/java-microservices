package com.bootcamp.trade_service.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TradeCreatedMessage {
    private Long tradeId;
    private Long receiverId;
    private String receiverPokemonId;
    private Long requesterId;
    private String requesterPokemonId;
}
