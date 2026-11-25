package com.example.orders.service;

import com.example.events.OrderCreatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCreatedEventRelay {

    private final OrderEventPublisher eventPublisher;

    public OrderCreatedEventRelay(OrderEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishAfterCommit(OrderCreatedEvent event) {
        eventPublisher.publish(event);
    }
}
