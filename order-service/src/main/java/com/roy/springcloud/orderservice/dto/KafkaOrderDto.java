package com.roy.springcloud.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class KafkaOrderDto implements Serializable {
    private Schema schema;
    private Payload payload;
}
