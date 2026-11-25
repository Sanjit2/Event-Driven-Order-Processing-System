package com.example.inventory.service;

import com.example.events.InventoryRejectedEvent;
import com.example.events.InventoryReservedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaInventoryEventPublisher implements InventoryEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaInventoryEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishReserved(InventoryReservedEvent event) {
        publish("inventory-reserved", event.orderId().toString(), event);
    }

    @Override
    public void publishRejected(InventoryRejectedEvent event) {
        publish("inventory-rejected", event.orderId().toString(), event);
    }

    private void publish(String topic, String key, Object event) {
        try {
            kafkaTemplate.send(topic, key, objectMapper.writeValueAsString(event)).join();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize inventory event", e);
        }
    }
}
