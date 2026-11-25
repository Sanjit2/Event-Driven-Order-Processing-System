package com.example.payment.service;

import com.example.events.InventoryReservedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InventoryReservedListener {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    public InventoryReservedListener(ObjectMapper objectMapper, PaymentService paymentService) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "inventory-reserved", groupId = "payment-service")
    public void onInventoryReserved(String payload) {
        try {
            paymentService.processPayment(objectMapper.readValue(payload, InventoryReservedEvent.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not deserialize InventoryReserved event", e);
        }
    }
}
