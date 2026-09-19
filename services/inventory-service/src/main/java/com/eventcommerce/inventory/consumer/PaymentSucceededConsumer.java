package com.eventcommerce.inventory.consumer;

import com.eventcommerce.contracts.events.InventoryFailedEvent;
import com.eventcommerce.contracts.events.InventoryReservedEvent;
import com.eventcommerce.contracts.events.PaymentSucceededEvent;
import com.eventcommerce.inventory.idempotency.ProcessedEvent;
import com.eventcommerce.inventory.idempotency.ProcessedEventRepository;
import com.eventcommerce.inventory.producer.InventoryEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PaymentSucceededConsumer {

    private final ObjectMapper objectMapper;
    private final InventoryEventProducer inventoryEventProducer;
    private final ProcessedEventRepository processedEventRepository;
    private static final String CONSUMER_NAME = "inventory-service.payment-succeeded";
    private static final Logger log = LoggerFactory.getLogger(PaymentSucceededConsumer.class);
    public PaymentSucceededConsumer(
            ObjectMapper objectMapper,
            InventoryEventProducer inventoryEventProducer,
            ProcessedEventRepository processedEventRepository) {

        this.objectMapper = objectMapper;
        this.inventoryEventProducer = inventoryEventProducer;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(
            topics = "payment-succeeded",
            groupId = "inventory-service"
    )
    @Transactional
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
            if(processedEventRepository.existsById(event.getEventId())){
                System.out.println("Skkiping already processed PaymentSucceededEvent"+event.getEventId());
                return;
            }
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
                                UUID.randomUUID().toString(),

                                event.getOrderId(),
                                event.getProductId(),
                                event.getQuantity(),
                                "Insufficient inventory"
                        );

                inventoryEventProducer.publishInventoryFailed(
                        failedEvent
                );
                processedEventRepository.save(new ProcessedEvent(event.getEventId(),CONSUMER_NAME, LocalDateTime.now()));
                System.out.println(
                        "Inventory reservation FAILED. orderId="
                                + event.getOrderId()
                );

                return;
            }

            InventoryReservedEvent reservedEvent =
                    new InventoryReservedEvent(
                            UUID.randomUUID().toString(),
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
            processedEventRepository.save(new ProcessedEvent(event.getEventId(),CONSUMER_NAME, LocalDateTime.now()));


        } catch (Exception e) {

            System.err.println(
                    "Failed to process PaymentSucceededEvent"
            );

            e.printStackTrace();
        }
    }
}