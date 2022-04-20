package com.roy.springcloud.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private String email;
    private String password;
    private String name;
    private String userId;
    private LocalDateTime createdAt;
    private String encryptedPassword;
}
