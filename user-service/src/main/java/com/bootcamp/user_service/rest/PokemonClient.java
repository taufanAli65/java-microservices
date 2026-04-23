package com.bootcamp.user_service.rest;

import com.bootcamp.user_service.dto.response.BaseResponse;
import com.bootcamp.user_service.dto.response.ResGetProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "pokemon-service",
        path = "/pokemon-service"
)
public interface PokemonClient {
    @GetMapping("/api/products/{id}")
    ResponseEntity<BaseResponse<ResGetProductDto>> getDetailPokemon(
            @PathVariable("id") String id);
}
