package com.bootcamp.user_service.controller;


import com.bootcamp.user_service.dto.request.ReqCreateUserDto;
import com.bootcamp.user_service.dto.request.ReqLoginUserDto;
import com.bootcamp.user_service.dto.response.BaseResponse;
import com.bootcamp.user_service.dto.response.ResCreateUserDto;
import com.bootcamp.user_service.dto.response.ResLoginDto;
import com.bootcamp.user_service.entity.UserEntity;
import com.bootcamp.user_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.hibernate.mapping.Any;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final HttpServletRequest request;

    public UserController(UserService userService, HttpServletRequest request) {
        this.userService = userService;
        this.request = request;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<ResCreateUserDto>> createUser(
            @Valid @RequestBody ReqCreateUserDto request
            ) {
        UserEntity user = userService.createUser(request);

        BaseResponse<ResCreateUserDto> response = BaseResponse.success(new ResCreateUserDto(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber()
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<ResLoginDto>> login(
            @Valid @RequestBody ReqLoginUserDto request
            ) {
        ResLoginDto data = userService.login(request);
        BaseResponse<ResLoginDto> response = BaseResponse.success(new ResLoginDto(
                data.getEmail(),
                data.getToken()
        ));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ResCreateUserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody ReqCreateUserDto request
    ) {
        ResCreateUserDto updatedUser = userService.updateUser(id, request);
        BaseResponse<ResCreateUserDto> response = BaseResponse.success("Success update user", updatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ResCreateUserDto>> getUserById (
            @PathVariable Long id
    ) {
        ResCreateUserDto respCreateuser = userService.getUserById(id);
        BaseResponse<ResCreateUserDto> response = BaseResponse.success("Success get user by id", respCreateuser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<BaseResponse<ResCreateUserDto>> getUserDetail () {
        Long userId = Long.parseLong(request.getHeader("X-Authenticated-User-Id"));
        ResCreateUserDto respCreateuser = userService.getUserById(userId);
        BaseResponse<ResCreateUserDto> response = BaseResponse.success("Success get user detail", respCreateuser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<ResCreateUserDto>>> getUserByFirstName(
            @RequestParam("name") String name
    ) {
        List<ResCreateUserDto> users = userService.getUserByFirstName(name);
        BaseResponse<List<ResCreateUserDto>> response = BaseResponse.success("Success get users by first name", users);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteById(
            @PathVariable Long id
    ) {
        userService.deleteById(id);
        BaseResponse<Void> response = BaseResponse.success("User Deleted", null);
        return ResponseEntity.ok(response);
    }
}
