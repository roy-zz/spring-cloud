package com.roy.springcloud.orderservice.service;

import com.roy.springcloud.orderservice.dto.OrderDto;

public interface KafkaProducer {
    OrderDto send(String kafkaTopic, OrderDto orderDto);
}
