package com.bootcamp.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqCreateUserDto {

    @NotBlank(message = "Email wajib diisi yeah")
    private String email;

    @NotBlank(message = "First Name wajib diisi yeah")
    private String firstName;

    @NotBlank(message = "Last Name wajib diisi yeah")
    private String lastName;

    @NotBlank(message = "Nomermu jangan kosong bos")
    private String phoneNumber;

    @NotBlank(message = "Password jg diisi bos")
    private String password;
}
