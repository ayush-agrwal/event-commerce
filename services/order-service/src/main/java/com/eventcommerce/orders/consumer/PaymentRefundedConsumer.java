package com.eventcommerce.orders.consumer;

import com.eventcommerce.contracts.events.PaymentRefundedEvent;
import com.eventcommerce.orders.model.Order;
import com.eventcommerce.orders.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentRefundedConsumer {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    public PaymentRefundedConsumer(
            ObjectMapper objectMapper,
            OrderRepository orderRepository) {

        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
    }

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

            order.setStatus("CANCELLED");

            orderRepository.save(order);

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