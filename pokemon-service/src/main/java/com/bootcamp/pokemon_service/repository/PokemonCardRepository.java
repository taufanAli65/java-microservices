package com.bootcamp.pokemon_service.repository;

import com.bootcamp.pokemon_service.entity.PokemonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonCardRepository extends JpaRepository<PokemonEntity, String> {
}
