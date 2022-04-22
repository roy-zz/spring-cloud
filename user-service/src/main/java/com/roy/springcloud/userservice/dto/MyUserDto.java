package com.roy.springcloud.userservice.dto;

import com.roy.springcloud.userservice.vo.response.OrderResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MyUserDto {
    private String email;
    private String password;
    private String name;
    private String userId;
    private LocalDateTime createdAt;
    private String encryptedPassword;
    private List<OrderResponse> orders = new ArrayList<>();
}
