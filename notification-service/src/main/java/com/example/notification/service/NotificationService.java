package com.example.notification.service;

import com.example.events.InventoryRejectedEvent;
import com.example.events.OrderCreatedEvent;
import com.example.events.PaymentFailedEvent;
import com.example.events.PaymentSuccessfulEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void orderCreated(OrderCreatedEvent event) {
        logger.info("Order created: orderId={}, customerId={}", event.orderId(), event.customerId());
    }

    public void inventoryRejected(InventoryRejectedEvent event) {
        logger.warn("Order failed: orderId={}, inventory reason={}", event.orderId(), event.reason());
    }

    public void paymentSuccessful(PaymentSuccessfulEvent event) {
        logger.info("Order confirmed: orderId={}, paymentId={}", event.orderId(), event.paymentId());
    }

    public void paymentFailed(PaymentFailedEvent event) {
        logger.warn("Payment failed: orderId={}, reason={}", event.orderId(), event.reason());
    }
}
