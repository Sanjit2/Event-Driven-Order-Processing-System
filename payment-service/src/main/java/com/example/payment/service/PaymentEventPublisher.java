package com.example.payment.service;

import com.example.events.PaymentFailedEvent;
import com.example.events.PaymentSuccessfulEvent;

public interface PaymentEventPublisher {
    void publishSuccessful(PaymentSuccessfulEvent event);

    void publishFailed(PaymentFailedEvent event);
}
