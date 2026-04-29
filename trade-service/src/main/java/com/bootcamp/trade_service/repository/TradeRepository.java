package com.bootcamp.trade_service.repository;

import com.bootcamp.trade_service.entity.TradeHistoryEntity;
import com.bootcamp.trade_service.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeRepository extends JpaRepository<TradeHistoryEntity, Long> {
    @Query("""
	    SELECT COUNT(t) > 0
	    FROM TradeHistoryEntity t
	    WHERE (t.status = :status AND t.requesterId = :requesterId AND t.requesterPokemonId = :requesterPokemonId)
	       OR (t.status = :status AND t.receiverId = :receiverId AND t.receiverPokemonId = :receiverPokemonId)
	    """)
    boolean existsPendingTradeForOwnerPokemon(
	    @Param("status") TradeStatus status,
	    @Param("requesterId") Long requesterId,
	    @Param("requesterPokemonId") String requesterPokemonId,
	    @Param("receiverId") Long receiverId,
	    @Param("receiverPokemonId") String receiverPokemonId
    );
}
