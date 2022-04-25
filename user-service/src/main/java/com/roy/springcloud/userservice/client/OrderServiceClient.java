package com.roy.springcloud.userservice.client;

import com.roy.springcloud.userservice.vo.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders")
    List<OrderResponse> getOrders(@PathVariable String userId);

}
