package com.roy.springcloud.orderservice.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Payload implements Serializable {
    private String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private int totalPrice;
    private int unitPrice;
}
