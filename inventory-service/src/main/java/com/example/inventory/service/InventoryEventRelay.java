package com.example.inventory.service;

import com.example.events.InventoryRejectedEvent;
import com.example.events.InventoryReservedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class InventoryEventRelay {

    private final InventoryEventPublisher eventPublisher;

    public InventoryEventRelay(InventoryEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishReservedAfterCommit(InventoryReservedEvent event) {
        eventPublisher.publishReserved(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishRejectedAfterCommit(InventoryRejectedEvent event) {
        eventPublisher.publishRejected(event);
    }
}
