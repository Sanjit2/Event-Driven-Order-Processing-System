package com.example.payment.service;

import com.example.events.InventoryReservedEvent;
import com.example.events.PaymentFailedEvent;
import com.example.events.PaymentSuccessfulEvent;
import com.example.payment.api.PaymentResponse;
import com.example.payment.model.Payment;
import com.example.payment.model.PaymentStatus;
import com.example.payment.repository.PaymentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentOutcomeSimulator outcomeSimulator;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentOutcomeSimulator outcomeSimulator,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.paymentRepository = paymentRepository;
        this.outcomeSimulator = outcomeSimulator;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public PaymentResponse processPayment(InventoryReservedEvent event) {
        Payment payment = paymentRepository.findByOrderId(event.orderId())
                .orElseGet(() -> {
                    PaymentStatus status = outcomeSimulator.isSuccessful()
                            ? PaymentStatus.SUCCESSFUL
                            : PaymentStatus.FAILED;
                    return paymentRepository.save(new Payment(
                            UUID.randomUUID(),
                            event.orderId(),
                            event.amount(),
                            status
                    ));
                });

        publishResult(payment);
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment not found"));
    }

    private void publishResult(Payment payment) {
        if (payment.getStatus() == PaymentStatus.SUCCESSFUL) {
            applicationEventPublisher.publishEvent(new PaymentSuccessfulEvent(
                    UUID.randomUUID(),
                    payment.getOrderId(),
                    payment.getId(),
                    payment.getAmount(),
                    Instant.now()
            ));
        } else {
            applicationEventPublisher.publishEvent(new PaymentFailedEvent(
                    UUID.randomUUID(),
                    payment.getOrderId(),
                    payment.getId(),
                    payment.getAmount(),
                    "Simulated payment failure",
                    Instant.now()
            ));
        }
    }
}
