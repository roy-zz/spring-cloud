package com.roy.springcloud.orderservice.service;

import com.roy.springcloud.orderservice.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);
    OrderDto getOrderByOrderId(String orderId);
    List<OrderDto> getOrderByUserId(String userId);
}
