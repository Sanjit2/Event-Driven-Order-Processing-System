package com.example.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(
        UUID eventId,
        UUID orderId,
        UUID paymentId,
        BigDecimal amount,
        String reason,
        Instant occurredAt
) {
}
