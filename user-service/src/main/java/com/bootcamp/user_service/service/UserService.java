package com.bootcamp.user_service.service;

import com.bootcamp.user_service.dto.request.ReqCreateUserDto;
import com.bootcamp.user_service.dto.response.ResCreateUserDto;
import com.bootcamp.user_service.dto.response.ResGetProductDto;
import com.bootcamp.user_service.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity createUser(ReqCreateUserDto request);
    ResCreateUserDto updateUser(Long id, ReqCreateUserDto request);

    ResCreateUserDto getUserById(Long id);
    List<ResCreateUserDto> getUserByFirstName(String name);
    void deleteById(Long id);

    ResGetProductDto getProductById(String id);
}
