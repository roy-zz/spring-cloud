package com.roy.springcloud.catalogservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roy.springcloud.catalogservice.domain.Catalog;
import com.roy.springcloud.catalogservice.repository.CatalogRepository;
import com.roy.springcloud.catalogservice.service.KafkaConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerImpl implements KafkaConsumer {
    private final CatalogRepository catalogRepository;

    @Override
    @Transactional
    @KafkaListener(topics = "example-order-topic")
    public void processMessage(String kafkaMessage) {
        log.info("Kafka Message: ======> {}", kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String targetProduceId = (String) Optional.of(map.get("productId"))
                .orElseThrow(() -> new IllegalStateException("Not found produceId"));
        Catalog savedCatalog = catalogRepository.findByProductId(targetProduceId)
                .orElseThrow(() -> new IllegalStateException("Not found catalog"));
        Integer soldQuantity = (Integer) Optional.of(map.get("quantity"))
                .orElseThrow(() -> new IllegalStateException("Not found quantity"));
        savedCatalog.setStock(savedCatalog.getStock() - soldQuantity);
    }

}
