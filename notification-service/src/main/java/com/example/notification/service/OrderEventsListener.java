package com.example.notification.service;

import com.example.events.InventoryRejectedEvent;
import com.example.events.OrderCreatedEvent;
import com.example.events.PaymentFailedEvent;
import com.example.events.PaymentSuccessfulEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrderEventsListener {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public OrderEventsListener(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "order-created", groupId = "notification-service")
    public void onOrderCreated(String payload) {
        notificationService.orderCreated(readEvent(payload, OrderCreatedEvent.class));
    }

    @KafkaListener(topics = "inventory-rejected", groupId = "notification-service")
    public void onInventoryRejected(String payload) {
        notificationService.inventoryRejected(readEvent(payload, InventoryRejectedEvent.class));
    }

    @KafkaListener(topics = "payment-successful", groupId = "notification-service")
    public void onPaymentSuccessful(String payload) {
        notificationService.paymentSuccessful(readEvent(payload, PaymentSuccessfulEvent.class));
    }

    @KafkaListener(topics = "payment-failed", groupId = "notification-service")
    public void onPaymentFailed(String payload) {
        notificationService.paymentFailed(readEvent(payload, PaymentFailedEvent.class));
    }

    private <T> T readEvent(String payload, Class<T> eventType) {
        try {
            return objectMapper.readValue(payload, eventType);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not deserialize notification event", e);
        }
    }
}
