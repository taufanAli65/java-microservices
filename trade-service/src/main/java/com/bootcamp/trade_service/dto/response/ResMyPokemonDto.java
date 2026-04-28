package com.bootcamp.trade_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResMyPokemonDto {
    private String id;
    private String name;
    private String rarity;
}
