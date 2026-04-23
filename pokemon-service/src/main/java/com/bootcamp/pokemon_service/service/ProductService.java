package com.bootcamp.pokemon_service.service;

import com.bootcamp.pokemon_service.dto.response.ResGetProductDto;

public interface ProductService {
    void syncDataByThirdPartyApi();
    ResGetProductDto getProductById(String id);
}
