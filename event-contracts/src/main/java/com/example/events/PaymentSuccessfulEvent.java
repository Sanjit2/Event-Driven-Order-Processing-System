package com.example.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentSuccessfulEvent(
        UUID eventId,
        UUID orderId,
        UUID paymentId,
        BigDecimal amount,
        Instant occurredAt
) {
}
