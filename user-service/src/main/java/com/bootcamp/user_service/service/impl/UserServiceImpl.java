package com.bootcamp.user_service.service.impl;

import com.bootcamp.user_service.dto.request.ReqCreateUserDto;
import com.bootcamp.user_service.dto.request.ReqLoginUserDto;
import com.bootcamp.user_service.dto.response.BaseResponse;
import com.bootcamp.user_service.dto.response.ResCreateUserDto;
import com.bootcamp.user_service.dto.response.ResGetProductDto;
import com.bootcamp.user_service.dto.response.ResLoginDto;
import com.bootcamp.user_service.entity.UserEntity;
import com.bootcamp.user_service.execption.BadRequestException;
import com.bootcamp.user_service.execption.DataNotFoundException;
import com.bootcamp.user_service.repository.UserRepository;
import com.bootcamp.user_service.rest.PokemonClient;
import com.bootcamp.user_service.service.UserService;
import com.bootcamp.user_service.util.JwtUtil;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PokemonClient pokemonClient;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PokemonClient pokemonClient, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.pokemonClient = pokemonClient;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserEntity createUser(ReqCreateUserDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email sudah digunakan");
        }
        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());
        return userRepository.save(user);
    }

    @Override
    public ResLoginDto login(ReqLoginUserDto request) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new DataNotFoundException("User Tidak Ditemukan");
        }
        UserEntity user = userOpt.get();
        if(request.getPassword().equals(user.getPassword()));
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        return new ResLoginDto(
                user.getEmail(),
                token
        );
    }

    @Override
    public ResCreateUserDto updateUser(Long id, ReqCreateUserDto request) {
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new DataNotFoundException("User Tidak Ditemukan");
        }

        UserEntity user = userOpt.get();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        UserEntity updatedUser = userRepository.save(user);
        return new ResCreateUserDto(
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getPhoneNumber()
        );
    }

    @Override
    public ResCreateUserDto getUserById(Long id) {
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new DataNotFoundException("User Tidak Ditemukan");
        }
        UserEntity user = userOpt.get();
        return new ResCreateUserDto(
                user.getEmail(), user.getFirstName(), user.getLastName(), user.getPhoneNumber()
        );
    }

    @Override
    public List<ResCreateUserDto> getUserByFirstName(String name) {
        Optional<List<UserEntity>> usersOpt = userRepository.findUserByFirstName(name);
        if (usersOpt.isEmpty() || usersOpt.get().isEmpty()) {
            throw new DataNotFoundException("User Tidak Ditemukan");
        }
        List<UserEntity> users = usersOpt.get();
        return users.stream()
                .map(user -> new ResCreateUserDto(
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber()
                ))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        Boolean isUserExist = userRepository.existsById(id);

        if (!isUserExist) {
            throw new DataNotFoundException("Data not found");
        }

        userRepository.deleteById(id);
    }

    @Override
    public ResGetProductDto getProductById(String id) {
        try{
            BaseResponse<ResGetProductDto> pokemon = pokemonClient.getDetailPokemon(id).getBody();
            ResGetProductDto response = pokemon.getData();
            return new ResGetProductDto(
                    response.getId(),
                    response.getName(),
                    response.getRarity()
            );
        } catch(FeignException.NotFound e) {
            throw new DataNotFoundException("Pokemon Tidak Ditemukan");
        }
    }
}
