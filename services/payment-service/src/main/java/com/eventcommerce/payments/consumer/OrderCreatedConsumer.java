package com.eventcommerce.payments.consumer;

import com.eventcommerce.contracts.events.OrderCreatedEvent;
import com.eventcommerce.payments.model.Payment;
import com.eventcommerce.payments.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    public OrderCreatedConsumer(
            ObjectMapper objectMapper,
            PaymentService paymentService) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }

    @KafkaListener(
            topics = "order-created",
            groupId = "payment-service"
    )
    public void consume(String message) {

        try {
            OrderCreatedEvent event =
                    objectMapper.readValue(message, OrderCreatedEvent.class);

            System.out.println("Received OrderCreatedEvent");
            System.out.println("Order ID: " + event.getOrderId());
            System.out.println("Customer ID: " + event.getCustomerId());
            System.out.println("Product ID: " + event.getProductId());
            System.out.println("Quantity: " + event.getQuantity());

            Payment payment = new Payment();
            payment.setOrderId(event.getOrderId());

            payment.setAmount(event.getAmount().doubleValue());
            payment.setProductId(event.getProductId());
            payment.setQuantity(event.getQuantity());
            Payment createdPayment =
                    paymentService.createPayment(payment);

            System.out.println(
                    "Payment created successfully. paymentId="
                            + createdPayment.getId()
                            + ", orderId="
                            + createdPayment.getOrderId()
            );

        } catch (Exception e) {
            System.err.println("Failed to process order-created event");
            e.printStackTrace();
        }
    }
}
