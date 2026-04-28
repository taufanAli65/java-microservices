package com.bootcamp.pokemon_service.repository;

import com.bootcamp.pokemon_service.entity.UserPokemonEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface UserPokemonRepository extends JpaRepository<UserPokemonEntity, Long> {
    boolean existsByUserId(Long userId);

    @EntityGraph(attributePaths = {"pokemonId"})
    List<UserPokemonEntity> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"pokemonId"})
    List<UserPokemonEntity> findByUserIdAndPokemonId_Rarity(Long userId, String rarity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"pokemonId"})
    Optional<UserPokemonEntity> findByUserIdAndPokemonId_Id(Long userId, String pokemonId);
}
