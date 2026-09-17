package com.eventcommerce.payments.producer;

import com.eventcommerce.contracts.events.PaymentRefundedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentRefundEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PaymentRefundEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishPaymentRefunded(
            PaymentRefundedEvent event) {

        try {

            String message =
                    objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    "payment-refunded",
                    String.valueOf(event.getOrderId()),
                    message
            );

            System.out.println(
                    "Published PaymentRefundedEvent for orderId="
                            + event.getOrderId()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to publish PaymentRefundedEvent",
                    e
            );
        }
    }
}