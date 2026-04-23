package com.bootcamp.user_service.controller;

import com.bootcamp.user_service.dto.response.BaseResponse;
import com.bootcamp.user_service.dto.response.ResGetProductDto;
import com.bootcamp.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class PokemonController {
    private final UserService userService;

    public PokemonController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ResGetProductDto>> getDetailPokemon(
            @PathVariable String id
    ) {
        ResGetProductDto product = userService.getProductById(id);
        BaseResponse<ResGetProductDto> response = BaseResponse.success("Pokemon berhasil ditemukan", new ResGetProductDto(
                product.getId(),
                product.getName(),
                product.getRarity()
        ));
        return ResponseEntity.ok(response);
    }

}
