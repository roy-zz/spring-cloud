package com.roy.springcloud.orderservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roy.springcloud.orderservice.dto.*;
import com.roy.springcloud.orderservice.service.OrderProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducerImpl implements OrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private List<Field> fields = List.of(
            new Field("string", true, "order_id"),
            new Field("string", true, "user_id"),
            new Field("string", true, "product_id"),
            new Field("int32", true, "quantity"),
            new Field("int32", true, "total_price"),
            new Field("int32", true, "unit_price"));
    private Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("orders")
            .build();

    @Override
    public OrderDto send(String topic, OrderDto orderDto) {
        Payload payload = Payload.builder()
                .orderId(orderDto.getOrderId())
                .userId(orderDto.getUserId())
                .productId(orderDto.getProductId())
                .quantity(orderDto.getQuantity())
                .unitPrice(orderDto.getUnitPrice())
                .totalPrice(orderDto.getTotalPrice())
                .build();

        KafkaOrderDto kafkaOrderDto = new KafkaOrderDto(schema, payload);
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = Strings.EMPTY;
        try {
            jsonString = mapper.writeValueAsString(kafkaOrderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        kafkaTemplate.send(topic, jsonString);
        log.info("order Producer sent data from the order microservice: {}", kafkaOrderDto);
        return orderDto;
    }
}
