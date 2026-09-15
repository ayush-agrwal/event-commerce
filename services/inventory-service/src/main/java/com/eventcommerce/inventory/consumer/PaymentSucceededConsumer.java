package com.eventcommerce.inventory.consumer;

import com.eventcommerce.contracts.events.InventoryReservedEvent;
import com.eventcommerce.contracts.events.PaymentSucceededEvent;
import com.eventcommerce.inventory.producer.InventoryEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentSucceededConsumer {

    private final ObjectMapper objectMapper;
    private final InventoryEventProducer inventoryEventProducer;

    public PaymentSucceededConsumer(
            ObjectMapper objectMapper,
            InventoryEventProducer inventoryEventProducer) {

        this.objectMapper = objectMapper;
        this.inventoryEventProducer = inventoryEventProducer;
    }

    @KafkaListener(
            topics = "payment-succeeded",
            groupId = "inventory-service"
    )
    public void consume(String message) {

        try {
            PaymentSucceededEvent event =
                    objectMapper.readValue(
                            message,
                            PaymentSucceededEvent.class
                    );

            System.out.println("Received PaymentSucceededEvent");
            System.out.println("Payment ID: " + event.getPaymentId());
            System.out.println("Order ID: " + event.getOrderId());
            System.out.println("Amount: " + event.getAmount());

            // For now, assume inventory reservation succeeds.
            // Actual stock validation will be added later.

            InventoryReservedEvent reservedEvent =
                    new InventoryReservedEvent(
                            event.getOrderId(),
                            event.getProductId(),
                            event.getQuantity()
                    );

            inventoryEventProducer.publishInventoryReserved(
                    reservedEvent
            );

            System.out.println(
                    "Inventory reserved successfully. orderId="
                            + event.getOrderId()
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to process payment-succeeded event"
            );

            e.printStackTrace();
        }
    }
}