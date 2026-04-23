package com.bootcamp.pokemon_service.controller;

import com.bootcamp.pokemon_service.dto.response.BaseResponse;
import com.bootcamp.pokemon_service.dto.response.ResGetProductDto;
import com.bootcamp.pokemon_service.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public BaseResponse<String> syncData() {
        productService.syncDataByThirdPartyApi();
        return BaseResponse.success("null");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ResGetProductDto>> getProduct (
            @PathVariable String id
    ){
            ResGetProductDto pokemon = productService.getProductById(id);
            BaseResponse<ResGetProductDto> response = BaseResponse.success("Pokemon telah ditemukan", pokemon);
            return ResponseEntity.ok(response);
        }
    }

