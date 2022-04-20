package com.roy.springcloud.catalogservice.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogResponse {
    private String productId;
    private String productName;
    private Integer stock;
    private Integer unitPrice;
    private LocalDateTime createdAt;
}
