package com.eventcommerce.payments.producer;

import com.eventcommerce.contracts.events.PaymentSucceededEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {

    private static final String PAYMENT_SUCCEEDED_TOPIC = "payment-succeeded";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PaymentEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishPaymentSucceeded(PaymentSucceededEvent event) {

        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    PAYMENT_SUCCEEDED_TOPIC,
                    event.getOrderId().toString(),
                    payload
            );

            System.out.println(
                    "Published PaymentSucceededEvent for orderId="
                            + event.getOrderId()
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Failed to serialize PaymentSucceededEvent", e);
        }
    }
}
