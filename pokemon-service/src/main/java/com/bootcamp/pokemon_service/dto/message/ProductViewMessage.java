package com.bootcamp.pokemon_service.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductViewMessage {
    private Integer userId;
    private String pokemonId;
    private String viewedAt;
}