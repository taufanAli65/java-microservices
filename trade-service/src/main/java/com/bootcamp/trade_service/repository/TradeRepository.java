package com.bootcamp.trade_service.repository;

import com.bootcamp.trade_service.entity.TradeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<TradeHistoryEntity, Long> {
}
