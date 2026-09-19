package com.eventcommerce.orders.consumer;

import com.eventcommerce.contracts.events.InventoryReservedEvent;
import com.eventcommerce.orders.idempotency.ProcessedEventRepository;
import com.eventcommerce.orders.model.Order;
import com.eventcommerce.orders.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.eventcommerce.orders.idempotency.ProcessedEvent;
import java.time.LocalDateTime;

@Component
public class InventoryReservedConsumer {

    private static final String CONSUMER_NAME="order-service.inventory-reserved";
    private  final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    public InventoryReservedConsumer(
            ObjectMapper objectMapper,
            OrderRepository orderRepository,
            ProcessedEventRepository processedEventRepository) {

        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    @KafkaListener(
            topics = "inventory-reserved",
            groupId = "order-service"
    )
    public void consume(String message) {

        try {
            InventoryReservedEvent event =
                    objectMapper.readValue(
                            message,
                            InventoryReservedEvent.class
                    );
            if(processedEventRepository.existsById(event.getEventId())) {
                System.out.println("Event already porcessed with this event"+event.getEventId());
                return;
            }
            System.out.println("Received InventoryReservedEvent");
            System.out.println("Order ID: " + event.getOrderId());
            System.out.println("Product ID: " + event.getProductId());
            System.out.println("Quantity: " + event.getQuantity());

            Order order = orderRepository
                    .findById(event.getOrderId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Order not found: " + event.getOrderId()
                            )
                    );

            order.setStatus("COMPLETED");

            orderRepository.save(order);
            processedEventRepository.save(new ProcessedEvent(event.getEventId(), CONSUMER_NAME, LocalDateTime.now()));
            System.out.println(
                    "Order completed successfully. orderId="
                            + order.getId()
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to process inventory-reserved event"
            );

            e.printStackTrace();
        }
    }
}