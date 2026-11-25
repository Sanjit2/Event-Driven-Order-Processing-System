package com.example.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        String customerId,
        String productId,
        int quantity,
        BigDecimal amount,
        Instant occurredAt
) {
}
