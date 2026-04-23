package com.bootcamp.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResCreateUserDto {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
