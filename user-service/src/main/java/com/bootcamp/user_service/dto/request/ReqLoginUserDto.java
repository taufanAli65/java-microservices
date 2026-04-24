package com.bootcamp.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqLoginUserDto {

    @NotBlank(message = "Email harus diisi")
    private String email;

    @NotBlank(message = "Password harus diisi")
    private String password;
}
