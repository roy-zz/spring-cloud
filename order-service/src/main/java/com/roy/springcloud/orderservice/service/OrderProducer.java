package com.roy.springcloud.orderservice.service;

import com.roy.springcloud.orderservice.dto.OrderDto;

public interface OrderProducer {
    OrderDto send(String topic, OrderDto orderDto);
}
