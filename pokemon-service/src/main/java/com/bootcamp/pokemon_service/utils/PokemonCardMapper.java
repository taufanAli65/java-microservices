package com.bootcamp.pokemon_service.utils;

import com.bootcamp.pokemon_service.dto.response.ResMyPokemonDto;
import com.bootcamp.pokemon_service.entity.PokemonEntity;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

public final class PokemonCardMapper {
    private PokemonCardMapper() {
    }

    public static PokemonEntity mapToEntity(JsonNode node) {
        Objects.requireNonNull(node, "node must not be null");

        PokemonEntity card = new PokemonEntity();
        JsonNode idNode = node.get("id");
        JsonNode nameNode = node.get("name");

        if (idNode != null && !idNode.isNull()) {
            card.setId(idNode.asText());
        }
        if (nameNode != null && !nameNode.isNull()) {
            card.setName(nameNode.asText());
        }

        JsonNode rarityNode = node.get("rarity");
        String rarity = (rarityNode == null || rarityNode.isNull()) ? "Common" : rarityNode.asText();
        if (rarity == null || rarity.isBlank()) {
            rarity = "Common";
        }
        card.setRarity(rarity);
        card.setRawData(node);

        return card;
    }

    public static ResMyPokemonDto toMyPokemonDto(PokemonEntity entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        return new ResMyPokemonDto(entity.getId(), entity.getName(), entity.getRarity());
    }
}
