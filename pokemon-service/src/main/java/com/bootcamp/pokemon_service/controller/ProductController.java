package com.bootcamp.pokemon_service.controller;

import com.bootcamp.pokemon_service.dto.response.BaseResponse;
import com.bootcamp.pokemon_service.dto.response.ResGetProductDto;
import com.bootcamp.pokemon_service.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<BaseResponse<String>>> syncData() {
        return productService.syncDataByThirdPartyApi()
                .thenApply(response -> response.getStatus()
                        ? ResponseEntity.ok(response)
                        : ResponseEntity.internalServerError().body(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ResGetProductDto>> getProduct(
            @PathVariable String id
    ) {
        ResGetProductDto pokemon = productService.getProductById(id);
        BaseResponse<ResGetProductDto> response = BaseResponse.success("Pokemon telah ditemukan", pokemon);
        return ResponseEntity.ok(response);
    }
}
