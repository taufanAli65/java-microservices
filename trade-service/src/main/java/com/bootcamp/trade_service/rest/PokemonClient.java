package com.bootcamp.trade_service.rest;

import com.bootcamp.trade_service.dto.response.BaseResponse;
import com.bootcamp.trade_service.dto.response.ResMyPokemonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "pokemon-service",
        path = "/pokemon-service"
)
public interface PokemonClient {
    @GetMapping("/api/pokemon/user/{userId}/pokemon")
    ResponseEntity<BaseResponse<List<ResMyPokemonDto>>> getReceiverPokemon(
            @PathVariable("userId") Long userId
    );

    @GetMapping("/api/pokemon/my-pokemon")
    ResponseEntity<BaseResponse<List<ResMyPokemonDto>>> getRequesterPokemon(
            @RequestHeader("X-Authenticated-User-Id") Long requesterId
    );
}
