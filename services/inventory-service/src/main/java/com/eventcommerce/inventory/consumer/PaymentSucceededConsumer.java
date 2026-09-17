package com.eventcommerce.inventory.consumer;

import com.eventcommerce.contracts.events.InventoryFailedEvent;
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

            System.out.println(
                    "Received PaymentSucceededEvent. orderId="
                            + event.getOrderId()
            );

            /*
             * Temporary failure simulation.
             *
             * Any product starting with FAIL- will
             * simulate an inventory reservation failure.
             */
            if (event.getProductId() != null
                    && event.getProductId().startsWith("FAIL-")) {

                InventoryFailedEvent failedEvent =
                        new InventoryFailedEvent(
                                event.getOrderId(),
                                event.getProductId(),
                                event.getQuantity(),
                                "Insufficient inventory"
                        );

                inventoryEventProducer.publishInventoryFailed(
                        failedEvent
                );

                System.out.println(
                        "Inventory reservation FAILED. orderId="
                                + event.getOrderId()
                );

                return;
            }

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
                    "Inventory reservation SUCCESS. orderId="
                            + event.getOrderId()
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to process PaymentSucceededEvent"
            );

            e.printStackTrace();
        }
    }
}