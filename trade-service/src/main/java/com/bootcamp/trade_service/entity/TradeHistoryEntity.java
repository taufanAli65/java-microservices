package com.bootcamp.trade_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "mst_trade_histories")
public class TradeHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "requester_pokemon_id", nullable = false)
    private String requesterPokemonId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "receiver_pokemon_id", nullable = false)
    private String receiverPokemonId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TradeStatus status;

    @CreationTimestamp
    @Column(name = "traded_at", nullable = false, updatable = false)
    private LocalDateTime tradedAt;
}
