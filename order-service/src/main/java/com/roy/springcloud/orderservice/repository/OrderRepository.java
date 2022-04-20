package com.roy.springcloud.orderservice.repository;

import com.roy.springcloud.orderservice.domain.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
    Iterable<Order> findAllByUserId(String userId);
}
