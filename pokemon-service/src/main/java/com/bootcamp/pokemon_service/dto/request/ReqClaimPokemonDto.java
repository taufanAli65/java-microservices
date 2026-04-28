package com.bootcamp.pokemon_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ReqClaimPokemonDto {

    @NotEmpty(message = "List pokemon tidak boleh kosong")
    @Size(min = 1, max = 3, message = "Maksimal memilih 3 pokemon")
    private List<@NotBlank(message = "pokemonId tidak boleh kosong") String> pokemons;
}
