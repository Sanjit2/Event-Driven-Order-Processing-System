package com.example.payment.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomPaymentOutcomeSimulator implements PaymentOutcomeSimulator {

    @Override
    public boolean isSuccessful() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
