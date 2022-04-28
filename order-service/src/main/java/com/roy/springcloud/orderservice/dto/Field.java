package com.roy.springcloud.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Field {
    private String type;
    private boolean optional;
    private String field;
}
