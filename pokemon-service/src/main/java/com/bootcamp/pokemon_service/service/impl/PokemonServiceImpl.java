package com.bootcamp.pokemon_service.service.impl;

import com.bootcamp.pokemon_service.dto.request.ReqClaimPokemonDto;
import com.bootcamp.pokemon_service.dto.response.ResClaimPokemon;
import com.bootcamp.pokemon_service.dto.response.ResMyPokemonDto;
import com.bootcamp.pokemon_service.entity.PokemonEntity;
import com.bootcamp.pokemon_service.entity.UserPokemonEntity;
import com.bootcamp.pokemon_service.exception.BadRequestException;
import com.bootcamp.pokemon_service.exception.DataNotFoundException;
import com.bootcamp.pokemon_service.repository.PokemonCardRepository;
import com.bootcamp.pokemon_service.repository.UserPokemonRepository;
import com.bootcamp.pokemon_service.service.PokemonService;
import com.bootcamp.pokemon_service.utils.PokemonCardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PokemonServiceImpl implements PokemonService {
    private final UserPokemonRepository userPokemonRepository;
    private final PokemonCardRepository pokemonCardRepository;

    public PokemonServiceImpl(UserPokemonRepository userPokemonRepository, PokemonCardRepository pokemonCardRepository) {
        this.userPokemonRepository = userPokemonRepository;
        this.pokemonCardRepository = pokemonCardRepository;
    }

    @Override
    public ResClaimPokemon claimPokemon(Long userId, ReqClaimPokemonDto request) {
        boolean isExist = userPokemonRepository.existsByUserId(userId);
        if (isExist) {
            throw new BadRequestException("User sudah pernah claim");
        }
        List<String> pokemonIds = request.getPokemons();
        List<PokemonEntity> validPokemonCards = pokemonCardRepository.findAllById(pokemonIds);

        if (validPokemonCards.isEmpty()) {
            throw new DataNotFoundException("Semua Pokemon ID tidak ditemukan");
        }

        List<UserPokemonEntity> claimedPokemons = validPokemonCards.stream().map(card -> {
            UserPokemonEntity userPokemon = new UserPokemonEntity();
            userPokemon.setUserId(userId);
            userPokemon.setPokemonId(card);
            return userPokemon;
        }).collect(Collectors.toList());

        userPokemonRepository.saveAll(claimedPokemons);

        List<ResClaimPokemon.PokemonDto> claimedPokemonList = validPokemonCards.stream()
            .map(card -> new ResClaimPokemon.PokemonDto(card.getId(), card.getName()))
            .collect(Collectors.toList());

        return new ResClaimPokemon(claimedPokemonList);
    }

    @Override
    public List<ResMyPokemonDto> getClaimedPokemon(Long userId, String rarity) {
        List<UserPokemonEntity> claimed;
        if (rarity == null || rarity.isBlank()) {
            claimed = userPokemonRepository.findByUserId(userId);
        } else {
            claimed = userPokemonRepository.findByUserIdAndPokemonId_Rarity(userId, rarity.trim());
        }

        if (claimed.isEmpty()) {
            throw new DataNotFoundException("User Tidak Ditemukan atau TIdak Memiliki Pokemon");
        }

        Comparator<ResMyPokemonDto> comparator = Comparator
                .comparing(ResMyPokemonDto::getId, Comparator.nullsLast(String::compareTo));

        return claimed.stream()
                .map(UserPokemonEntity::getPokemonId)
                .filter(Objects::nonNull)
                .map(PokemonCardMapper::toMyPokemonDto)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

}
