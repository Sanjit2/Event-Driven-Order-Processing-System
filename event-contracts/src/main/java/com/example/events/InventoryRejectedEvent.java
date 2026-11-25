package com.example.events;

import java.time.Instant;
import java.util.UUID;

public record InventoryRejectedEvent(
        UUID eventId,
        UUID orderId,
        String productId,
        int quantity,
        String reason,
        Instant occurredAt
) {
}
