package com.example.payment.service;

import com.example.events.InventoryReservedEvent;
import com.example.events.PaymentFailedEvent;
import com.example.events.PaymentSuccessfulEvent;
import com.example.payment.api.PaymentResponse;
import com.example.payment.model.Payment;
import com.example.payment.model.PaymentStatus;
import com.example.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentOutcomeSimulator outcomeSimulator;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void recordsAndPublishesSuccessfulPayment() {
        InventoryReservedEvent event = reservedEvent();
        when(paymentRepository.findByOrderId(event.orderId())).thenReturn(Optional.empty());
        when(outcomeSimulator.isSuccessful()).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.processPayment(event);

        assertEquals(PaymentStatus.SUCCESSFUL, response.status());
        assertEquals(event.amount(), response.amount());
        ArgumentCaptor<PaymentSuccessfulEvent> resultCaptor =
                ArgumentCaptor.forClass(PaymentSuccessfulEvent.class);
        verify(applicationEventPublisher).publishEvent(resultCaptor.capture());
        assertEquals(event.orderId(), resultCaptor.getValue().orderId());
    }

    @Test
    void recordsAndPublishesFailedPayment() {
        InventoryReservedEvent event = reservedEvent();
        when(paymentRepository.findByOrderId(event.orderId())).thenReturn(Optional.empty());
        when(outcomeSimulator.isSuccessful()).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.processPayment(event);

        assertEquals(PaymentStatus.FAILED, response.status());
        ArgumentCaptor<PaymentFailedEvent> resultCaptor =
                ArgumentCaptor.forClass(PaymentFailedEvent.class);
        verify(applicationEventPublisher).publishEvent(resultCaptor.capture());
        assertEquals(event.orderId(), resultCaptor.getValue().orderId());
        assertEquals("Simulated payment failure", resultCaptor.getValue().reason());
    }

    private InventoryReservedEvent reservedEvent() {
        return new InventoryReservedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                1,
                new BigDecimal("19.99"),
                Instant.now()
        );
    }
}
