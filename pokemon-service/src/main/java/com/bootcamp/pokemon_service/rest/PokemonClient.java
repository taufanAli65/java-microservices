package com.bootcamp.pokemon_service.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pokemonClient", url = "https://api.pokemontcg.io/v2")
public interface PokemonClient {
    @GetMapping("/cards")
    JsonNode searchCards(@RequestParam("q") String query);
}
