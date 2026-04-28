package com.bootcamp.trade_service.support;

public final class TradeKafkaTopics {
    public static final String TRADE_CREATED = "TRADECREATED";
    public static final String POKEMON_TRADE_SUCCESS = "POKEMONTRADESUCCESS";
    public static final String POKEMON_TRADE_FAILED = "POKEMONTRADEFAILED";
    public static final String UPDATE_POKEMON_TRADE_FAILED = "UPDATEPOKEMONTRADEFAILED";
    public static final String UPDATE_POKEMON_TRADE_FAILED_DLT = UPDATE_POKEMON_TRADE_FAILED + ".DLT";

    private TradeKafkaTopics() {
    }
}
