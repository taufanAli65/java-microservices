package com.bootcamp.user_service.controller;


import com.bootcamp.user_service.dto.request.ReqCreateUserDto;
import com.bootcamp.user_service.dto.response.BaseResponse;
import com.bootcamp.user_service.dto.response.ResCreateUserDto;
import com.bootcamp.user_service.entity.UserEntity;
import com.bootcamp.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
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
