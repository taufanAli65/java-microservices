package com.bootcamp.pokemon_service.service;

import com.bootcamp.pokemon_service.dto.response.BaseResponse;
import com.bootcamp.pokemon_service.dto.response.ResGetProductDto;

import java.util.concurrent.CompletableFuture;

public interface ProductService {
    CompletableFuture<BaseResponse<String>> syncDataByThirdPartyApi();
    ResGetProductDto getProductById(String id);
}
