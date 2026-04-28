package com.bootcamp.pokemon_service.service;

import com.bootcamp.pokemon_service.dto.request.ReqClaimPokemonDto;
import com.bootcamp.pokemon_service.dto.response.ResClaimPokemon;
import com.bootcamp.pokemon_service.dto.response.ResMyPokemonDto;

import java.util.List;

public interface PokemonService {
    ResClaimPokemon claimPokemon(Long userId, ReqClaimPokemonDto pokemons);

    List<ResMyPokemonDto> getClaimedPokemon(Long userId, String rarity);
}
