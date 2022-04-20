package com.roy.springcloud.catalogservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CatalogDto implements Serializable {
    private String productId;
    private String productName;
    private Integer stock;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalPrice;
    private LocalDateTime createdAt;

    private String orderId;
    private String userId;
}
