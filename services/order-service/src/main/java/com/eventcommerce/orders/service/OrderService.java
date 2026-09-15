package com.eventcommerce.orders.service;

import com.eventcommerce.contracts.events.OrderCreatedEvent;
import com.eventcommerce.orders.model.Order;
import com.eventcommerce.orders.outbox.OutboxEvent;
import com.eventcommerce.orders.outbox.OutboxEventRepository;
import com.eventcommerce.orders.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OrderService(
            OrderRepository orderRepository,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper) {

        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Order createOrder(Order order) {

        // 1. Set initial order status
        order.setStatus("CREATED");

        // 2. Save order
        Order savedOrder = orderRepository.save(order);

        // 3. Create OrderCreated event
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getProductId(),
                savedOrder.getQuantity(),
                savedOrder.getAmount()
        );

        try {

            // 4. Convert event to JSON
            String payload = objectMapper.writeValueAsString(event);

            // 5. Create Outbox event
            OutboxEvent outboxEvent = new OutboxEvent();

            outboxEvent.setEventId(UUID.randomUUID().toString());

            outboxEvent.setEventType("OrderCreated");

            outboxEvent.setAggregateType("Order");

            outboxEvent.setAggregateId(
                    savedOrder.getId().toString()
            );

            outboxEvent.setPayload(payload);

            outboxEvent.setStatus("NEW");

            outboxEvent.setRetryCount(0);

            outboxEvent.setCreatedAt(LocalDateTime.now());

            outboxEvent.setNextAttemptAt(LocalDateTime.now());

            // 6. Save Outbox event
            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to serialize OrderCreatedEvent",
                    e
            );
        }

        // 7. Return saved order
        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}
