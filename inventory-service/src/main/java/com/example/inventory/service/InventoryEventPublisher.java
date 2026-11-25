package com.example.inventory.service;

import com.example.events.InventoryRejectedEvent;
import com.example.events.InventoryReservedEvent;

public interface InventoryEventPublisher {
    void publishReserved(InventoryReservedEvent event);

    void publishRejected(InventoryRejectedEvent event);
}
