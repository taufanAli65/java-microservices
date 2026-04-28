package com.bootcamp.trade_service.dto.message;

import com.bootcamp.trade_service.entity.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TradeStatusRetryMessage {
    private Long tradeId;
    private Long receiverId;
    private String receiverPokemonId;
    private Long requesterId;
    private String requesterPokemonId;
    private TradeStatus targetStatus;
}
