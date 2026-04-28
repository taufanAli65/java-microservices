package com.bootcamp.trade_service.repository;

import com.bootcamp.trade_service.entity.TradeHistoryEntity;
import com.bootcamp.trade_service.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<TradeHistoryEntity, Long> {
	boolean existsByStatusAndRequesterPokemonIdOrStatusAndReceiverPokemonId(
			TradeStatus requesterStatus,
			String requesterPokemonId,
			TradeStatus receiverStatus,
			String receiverPokemonId
	);
}
