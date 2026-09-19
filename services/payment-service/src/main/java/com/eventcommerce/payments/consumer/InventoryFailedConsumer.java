package com.eventcommerce.payments.consumer;

import com.eventcommerce.contracts.events.InventoryFailedEvent;
import com.eventcommerce.contracts.events.PaymentRefundedEvent;
import com.eventcommerce.payments.idempotency.ProcessedEvent;
import com.eventcommerce.payments.idempotency.ProcessedEventRepository;
import com.eventcommerce.payments.model.Payment;
import com.eventcommerce.payments.producer.PaymentRefundEventProducer;
import com.eventcommerce.payments.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class InventoryFailedConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final PaymentRefundEventProducer refundEventProducer;
    private static final String CONSUMER_NAME="payment-service.inventory-failed";
    private  final ProcessedEventRepository processedEventRepository;
    public InventoryFailedConsumer(
            ObjectMapper objectMapper,
            PaymentService paymentService,
            PaymentRefundEventProducer refundEventProducer, ProcessedEventRepository processedEventRepository) {

        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
        this.refundEventProducer = refundEventProducer;
        this.processedEventRepository = processedEventRepository;
    }
    @Transactional
    @KafkaListener(
            topics = "inventory-failed",
            groupId = "payment-service"
    )
    public void consume(String message) {

        try {

            InventoryFailedEvent event =
                    objectMapper.readValue(
                            message,
                            InventoryFailedEvent.class
                    );

            System.out.println(
                    "Received InventoryFailedEvent. orderId="
                            + event.getOrderId()
            );
            if(processedEventRepository.existsById(event.getEventId())){
                System.out.println("Skiping alredy-processed InventoryFailedEvent:"+event.getEventId());
                return;
            }
            Payment payment =
                    paymentService.findByOrderId(
                            event.getOrderId()
                    );

            if (payment == null) {
                throw new RuntimeException(
                        "Payment not found for orderId="
                                + event.getOrderId()
                );
            }

            payment.setStatus("REFUNDED");

            Payment savedPayment =
                    paymentService.updatePayment(payment);

            PaymentRefundedEvent refundedEvent =
                    new PaymentRefundedEvent(
                            UUID.randomUUID().toString(),
                            savedPayment.getId(),
                            savedPayment.getOrderId(),
                            BigDecimal.valueOf(
                                    savedPayment.getAmount()
                            )
                    );

            refundEventProducer.publishPaymentRefunded(
                    refundedEvent
            );

            System.out.println(
                    "Payment refunded successfully. orderId="
                            + event.getOrderId()
            );
            processedEventRepository.save(new ProcessedEvent(event.getEventId(), CONSUMER_NAME, LocalDateTime.now()));

        } catch (Exception e) {

            System.err.println(
                    "Failed to process InventoryFailedEvent"
            );

            e.printStackTrace();
        }
    }
}