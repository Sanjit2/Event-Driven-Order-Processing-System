package com.example.orders.service;

import com.example.events.InventoryRejectedEvent;
import com.example.events.PaymentFailedEvent;
import com.example.events.PaymentSuccessfulEvent;
import com.example.orders.model.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrderEventListener {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    public OrderEventListener(ObjectMapper objectMapper, OrderService orderService) {
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @KafkaListener(topics = "inventory-rejected", groupId = "order-service")
    public void onInventoryRejected(String payload) {
        InventoryRejectedEvent event = readEvent(payload, InventoryRejectedEvent.class);
        orderService.updateStatus(event.orderId(), OrderStatus.FAILED);
    }

    @KafkaListener(topics = "payment-successful", groupId = "order-service")
    public void onPaymentSuccessful(String payload) {
        PaymentSuccessfulEvent event = readEvent(payload, PaymentSuccessfulEvent.class);
        orderService.updateStatus(event.orderId(), OrderStatus.CONFIRMED);
    }

    @KafkaListener(topics = "payment-failed", groupId = "order-service")
    public void onPaymentFailed(String payload) {
        PaymentFailedEvent event = readEvent(payload, PaymentFailedEvent.class);
        orderService.updateStatus(event.orderId(), OrderStatus.FAILED);
    }

    private <T> T readEvent(String payload, Class<T> eventType) {
        try {
            return objectMapper.readValue(payload, eventType);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not deserialize Kafka event", e);
        }
    }
}
