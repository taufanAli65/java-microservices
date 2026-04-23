package com.bootcamp.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResGetProductDto {
    private String id;
    private String name;
    private String rarity;
}
