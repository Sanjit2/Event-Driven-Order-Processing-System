package com.example.payment.service;

import com.example.events.PaymentFailedEvent;
import com.example.events.PaymentSuccessfulEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentEventRelay {

    private final PaymentEventPublisher eventPublisher;

    public PaymentEventRelay(PaymentEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishSuccessfulAfterCommit(PaymentSuccessfulEvent event) {
        eventPublisher.publishSuccessful(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishFailedAfterCommit(PaymentFailedEvent event) {
        eventPublisher.publishFailed(event);
    }
}
