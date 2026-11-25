package com.example.inventory.service;

import com.example.events.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrderCreatedListener {

    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    public OrderCreatedListener(ObjectMapper objectMapper, InventoryService inventoryService) {
        this.objectMapper = objectMapper;
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order-created", groupId = "inventory-service")
    public void onOrderCreated(String payload) {
        try {
            inventoryService.reserveOrder(objectMapper.readValue(payload, OrderCreatedEvent.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not deserialize OrderCreated event", e);
        }
    }
}
