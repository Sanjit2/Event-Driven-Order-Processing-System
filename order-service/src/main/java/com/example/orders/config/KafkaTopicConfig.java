package com.example.orders.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name("order-created").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryReservedTopic() {
        return TopicBuilder.name("inventory-reserved").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryRejectedTopic() {
        return TopicBuilder.name("inventory-rejected").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic paymentSuccessfulTopic() {
        return TopicBuilder.name("payment-successful").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name("payment-failed").partitions(1).replicas(1).build();
    }
}
