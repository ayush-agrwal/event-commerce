package com.eventcommerce.orders.consumer;

import com.eventcommerce.contracts.events.PaymentRefundedEvent;
import com.eventcommerce.orders.idempotency.ProcessedEvent;
import com.eventcommerce.orders.idempotency.ProcessedEventRepository;
import com.eventcommerce.orders.model.Order;
import com.eventcommerce.orders.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentRefundedConsumer {

    private static final String CONSUMER_NAME =
            "order-service.payment-refunded";

    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    public PaymentRefundedConsumer(
            ObjectMapper objectMapper,
            OrderRepository orderRepository,
            ProcessedEventRepository processedEventRepository) {

        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    @KafkaListener(
            topics = "payment-refunded",
            groupId = "order-service"
    )
    public void consume(String message) {

        try {

            PaymentRefundedEvent event =
                    objectMapper.readValue(
                            message,
                            PaymentRefundedEvent.class
                    );

            // Idempotency check
            if (processedEventRepository.existsById(event.getEventId())) {

                System.out.println(
                        event.getEventId()
                                + " is already processed"
                );

                return;
            }

            System.out.println(
                    "Received PaymentRefundedEvent"
            );

            System.out.println(
                    "Order ID: " + event.getOrderId()
            );

            System.out.println(
                    "Payment ID: " + event.getPaymentId()
            );

            System.out.println(
                    "Refund Amount: " + event.getAmount()
            );

            Order order =
                    orderRepository
                            .findById(event.getOrderId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Order not found: "
                                                    + event.getOrderId()
                                    )
                            );

            // Update order status
            order.setStatus("CANCELLED");

            orderRepository.save(order);

            processedEventRepository.save(
                    new ProcessedEvent(
                            event.getEventId(),
                            CONSUMER_NAME,
                            LocalDateTime.now()
                    )
            );

            System.out.println(
                    "Order cancelled successfully. orderId="
                            + order.getId()
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to process payment-refunded event"
            );

            e.printStackTrace();
        }
    }
}
