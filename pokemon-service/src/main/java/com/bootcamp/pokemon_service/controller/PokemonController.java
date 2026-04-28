package com.bootcamp.pokemon_service.controller;

import com.bootcamp.pokemon_service.dto.request.ReqClaimPokemonDto;
import com.bootcamp.pokemon_service.dto.response.BaseResponse;
import com.bootcamp.pokemon_service.dto.response.ResClaimPokemon;
import com.bootcamp.pokemon_service.dto.response.ResMyPokemonDto;
import com.bootcamp.pokemon_service.service.PokemonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {
    private final PokemonService pokemonService;
    private final HttpServletRequest request;

    public PokemonController(PokemonService pokemonService, HttpServletRequest request) {
        this.pokemonService = pokemonService;
        this.request = request;
    }

    @GetMapping("/claim-starter")
    public ResponseEntity<BaseResponse<ResClaimPokemon>> claimPokemon(
            @Valid @RequestBody ReqClaimPokemonDto pokemon
            ) {
        Long userId = Long.parseLong(request.getHeader("X-Authenticated-User-Id"));
        ResClaimPokemon claimedPokemon = pokemonService.claimPokemon(userId, pokemon);
        BaseResponse<ResClaimPokemon> response = BaseResponse.success("Berhasil claim pokemon", claimedPokemon);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-pokemon")
    public ResponseEntity<BaseResponse<List<ResMyPokemonDto>>> getClaimedPokemon(
            @RequestParam(required = false) String rarity
    ) {
        Long userId =  Long.parseLong(request.getHeader("X-Authenticated-User-Id"));
        List<ResMyPokemonDto> data = pokemonService.getClaimedPokemon(userId, rarity);
        BaseResponse<List<ResMyPokemonDto>> response = BaseResponse.success("Berhasil ambil pokemon user", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/pokemon")
    public ResponseEntity<BaseResponse<List<ResMyPokemonDto>>> getUserPokemon(
            @PathVariable Long userId,
            @RequestParam(required = false) String rarity
    ) {
        List<ResMyPokemonDto> data = pokemonService.getClaimedPokemon(userId, rarity);
        BaseResponse<List<ResMyPokemonDto>> response = BaseResponse.success("Berhasil ambil pokemon user", data);
        return ResponseEntity.ok(response);
    }
}
