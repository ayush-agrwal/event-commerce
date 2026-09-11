package com.eventcommerce.inventory.producer;

import com.eventcommerce.contracts.events.InventoryReservedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventProducer {

    private static final String INVENTORY_RESERVED_TOPIC = "inventory-reserved";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public InventoryEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishInventoryReserved(InventoryReservedEvent event) {

        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    INVENTORY_RESERVED_TOPIC,
                    event.getOrderId().toString(),
                    payload
            );

            System.out.println(
                    "Published InventoryReservedEvent for orderId="
                            + event.getOrderId()
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Failed to serialize InventoryReservedEvent",
                    e
            );
        }
    }
}