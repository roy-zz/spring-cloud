package com.roy.springcloud.orderservice.vo.request;

import lombok.Data;

@Data
public class OrderSaveRequest {
    private String productId;
    private Integer quantity;
    private Integer unitPrice;
}
