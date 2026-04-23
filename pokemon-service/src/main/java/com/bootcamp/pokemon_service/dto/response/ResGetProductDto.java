package com.bootcamp.pokemon_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResGetProductDto {
    private String id;
    private String name;
    private String rarity;

    public ResGetProductDto(String id) {
    }
}
