package com.roy.springcloud.userservice.vo.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private String productId;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalPrice;
    private LocalDateTime createdAt;

    private String orderId;
}
