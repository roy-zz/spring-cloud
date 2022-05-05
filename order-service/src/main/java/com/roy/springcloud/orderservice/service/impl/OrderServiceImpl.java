package com.roy.springcloud.orderservice.service.impl;

import com.roy.springcloud.orderservice.domain.Order;
import com.roy.springcloud.orderservice.dto.OrderDto;
import com.roy.springcloud.orderservice.repository.OrderRepository;
import com.roy.springcloud.orderservice.service.KafkaProducer;
import com.roy.springcloud.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.roy.springcloud.orderservice.mapper.MapperUtil.toObject;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final KafkaProducer kafkaProducer;
    private final OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQuantity() * orderDto.getUnitPrice());
        Order newOrder = toObject(orderDto, Order.class);
        orderRepository.save(newOrder);
        kafkaProducer.send("example-order-topic", orderDto);
        return toObject(newOrder, OrderDto.class);
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        Order savedOrder = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot found order"));
        return toObject(savedOrder, OrderDto.class);
    }

    @Override
    public List<OrderDto> getOrderByUserId(String userId) {
        Iterable<Order> savedOrders = orderRepository.findAllByUserId(userId);
        List<OrderDto> response = new ArrayList<>();
        savedOrders.forEach(order -> {
            response.add(toObject(order, OrderDto.class));
        });
        return response;
    }

}
