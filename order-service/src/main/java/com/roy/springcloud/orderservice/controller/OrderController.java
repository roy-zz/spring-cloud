package com.roy.springcloud.orderservice.controller;

import com.roy.springcloud.orderservice.dto.OrderDto;
import com.roy.springcloud.orderservice.service.OrderService;
import com.roy.springcloud.orderservice.vo.request.OrderSaveRequest;
import com.roy.springcloud.orderservice.vo.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.roy.springcloud.util.mapper.MapperUtil.toObject;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service")
public class OrderController {
    private final Environment environment;
    private final OrderService orderService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return String.format("It's working in order service on port: %s",
                environment.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody OrderSaveRequest request) {
        OrderDto orderDto = toObject(request, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto savedOrder = orderService.createOrder(orderDto);
        OrderResponse response = toObject(savedOrder, OrderResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrder(@PathVariable("userId") String userId) {
        List<OrderDto> savedOrders = orderService.getOrderByUserId(userId);
        List<OrderResponse> response = savedOrders.stream()
                .map(order -> toObject(order, OrderResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
