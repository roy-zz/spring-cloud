package com.roy.springcloud.catalogservice.service;

public interface KafkaConsumer {
    void processMessage(String kafkaMessage);
}
