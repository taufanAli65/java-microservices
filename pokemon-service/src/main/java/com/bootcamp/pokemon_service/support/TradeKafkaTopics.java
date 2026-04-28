package com.bootcamp.pokemon_service.support;

public final class TradeKafkaTopics {
    public static final String TRADE_CREATED = "TRADECREATED";
    public static final String POKEMON_TRADE_SUCCESS = "POKEMONTRADESUCCESS";
    public static final String POKEMON_TRADE_FAILED = "POKEMONTRADEFAILED";

    private TradeKafkaTopics() {
    }
}
