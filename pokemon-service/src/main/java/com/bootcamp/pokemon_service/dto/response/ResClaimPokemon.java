package com.bootcamp.pokemon_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResClaimPokemon {

    private List<PokemonDto> pokemons;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PokemonDto {
        private String id;
        private String name;
    }
}