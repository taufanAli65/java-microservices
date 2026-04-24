package com.bootcamp.pokemon_service.service.impl;

import com.bootcamp.pokemon_service.dto.response.BaseResponse;
import com.bootcamp.pokemon_service.dto.response.ResGetProductDto;
import com.bootcamp.pokemon_service.entity.PokemonEntity;
import com.bootcamp.pokemon_service.exception.DataNotFoundException;
import com.bootcamp.pokemon_service.repository.PokemonCardRepository;
import com.bootcamp.pokemon_service.rest.PokemonClient;
import com.bootcamp.pokemon_service.service.ProductService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final PokemonCardRepository pokemonCardRepository;
    private final PokemonClient pokemonClient;

    public ProductServiceImpl(PokemonCardRepository pokemonCardRepository, PokemonClient pokemonClient) {
        this.pokemonCardRepository = pokemonCardRepository;
        this.pokemonClient = pokemonClient;
    }

    @Override
    public ResGetProductDto getProductById(String id) {

        PokemonEntity response = pokemonCardRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Pokemon tidak ditemukan")
        );

        return new ResGetProductDto(response.getId(), response.getName(), response.getRarity());
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<BaseResponse<String>> syncDataByThirdPartyApi() {
        try {
            JsonNode response = pokemonClient.searchCards("");
            JsonNode cardlist = response.get("data");

            if (cardlist == null || !cardlist.isArray()) {
                return CompletableFuture.completedFuture(
                        BaseResponse.error("Invalid pokemon data received from third party API")
                );
            }

            List<PokemonEntity> cards = new ArrayList<>();
            for (JsonNode node : cardlist) {
                try {
                    PokemonEntity card = mapToEntity(node);
                    cards.add(card);
                } catch (Exception e) {
                    log.warn("Failed to map pokemon card payload: {}", node, e);
                }
            }

            pokemonCardRepository.saveAll(cards);
            log.info("Done processing all pokemon data. Total saved: {}", cards.size());

            return CompletableFuture.completedFuture(
                    BaseResponse.success(
                            "Pokemon data synced successfully",
                            "Successfully synced " + cards.size() + " pokemon cards"
                    )
            );
        } catch (Exception e) {
            log.error("Failed to sync pokemon data from third party API", e);
            return CompletableFuture.completedFuture(
                    BaseResponse.error("Failed to sync pokemon data from third party API")
            );
        }
    }

    private PokemonEntity mapToEntity(JsonNode node) {
        PokemonEntity card = new PokemonEntity();
        card.setId(node.get("id").asText());
        card.setName(node.get("name").asText());
        card.setRarity(node.get("rarity").asText("Common"));
        card.setRawData(node);

        return card;
    }
}
