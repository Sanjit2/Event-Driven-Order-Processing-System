package com.example.payment.service;

import com.example.events.PaymentFailedEvent;
import com.example.events.PaymentSuccessfulEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPaymentEventPublisher implements PaymentEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaPaymentEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishSuccessful(PaymentSuccessfulEvent event) {
        publish("payment-successful", event.orderId().toString(), event);
    }

    @Override
    public void publishFailed(PaymentFailedEvent event) {
        publish("payment-failed", event.orderId().toString(), event);
    }

    private void publish(String topic, String key, Object event) {
        try {
            kafkaTemplate.send(topic, key, objectMapper.writeValueAsString(event)).join();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize payment event", e);
        }
    }
}
