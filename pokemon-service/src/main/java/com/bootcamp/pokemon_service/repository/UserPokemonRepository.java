package com.bootcamp.pokemon_service.repository;

import com.bootcamp.pokemon_service.entity.UserPokemonEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPokemonRepository extends JpaRepository<UserPokemonEntity, Long> {
    boolean existsByUserId(Long userId);

    @EntityGraph(attributePaths = {"pokemonId"})
    List<UserPokemonEntity> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"pokemonId"})
    List<UserPokemonEntity> findByUserIdAndPokemonId_Rarity(Long userId, String rarity);
}
