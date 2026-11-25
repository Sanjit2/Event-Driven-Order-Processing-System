package com.example.orders.service;

import com.example.orders.api.CreateOrderRequest;
import com.example.orders.api.OrderResponse;
import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;
import com.example.orders.repository.OrderRepository;
import com.example.events.OrderCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createsOrderAndPublishesOrderCreatedEvent() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CreateOrderRequest request = new CreateOrderRequest(
                "customer-1",
                "product-1",
                2,
                new BigDecimal("19.98")
        );

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response.id());
        assertEquals("customer-1", response.customerId());
        assertEquals("product-1", response.productId());
        assertEquals(2, response.quantity());
        assertEquals(new BigDecimal("19.98"), response.amount());
        assertEquals(OrderStatus.CREATED, response.status());

        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(response.id(), eventCaptor.getValue().orderId());
        assertEquals("customer-1", eventCaptor.getValue().customerId());
    }
}
