
package com.eventcommerce.orders.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboxEventPublisher {

    private static final String ORDER_CREATED_TOPIC = "order-created";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxEventPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate) {

        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        LocalDateTime now = LocalDateTime.now();

        List<OutboxEvent> events =
                outboxEventRepository
                        .findTop10ByStatusAndNextAttemptAtLessThanEqualOrderByIdAsc(
                                "NEW",
                                now
                        );

        for (OutboxEvent event : events) {

            try {

                System.out.println(
                        "Publishing outbox event. " +
                                "eventId=" + event.getEventId() +
                                ", outboxId=" + event.getId() +
                                ", retryCount=" + event.getRetryCount()
                );

                kafkaTemplate.send(
                        ORDER_CREATED_TOPIC,
                        event.getAggregateId(),
                        event.getPayload()
                ).get();

                // Kafka publish succeeded
                event.setStatus("PROCESSED");
                event.setProcessedAt(LocalDateTime.now());

                outboxEventRepository.save(event);

                System.out.println(
                        "Outbox event published successfully. " +
                                "eventId=" + event.getEventId()
                );

            } catch (Exception e) {

                int currentRetryCount =
                        event.getRetryCount() == null
                                ? 0
                                : event.getRetryCount();

                int nextRetryCount = currentRetryCount + 1;

                event.setRetryCount(nextRetryCount);

                /*
                 * Exponential backoff:
                 *
                 * Retry 1 -> 5 seconds
                 * Retry 2 -> 10 seconds
                 * Retry 3 -> 20 seconds
                 * Retry 4 -> 40 seconds
                 *
                 * Maximum delay = 5 minutes
                 */
                long delaySeconds =
                        Math.min(
                                300,
                                5L * (1L << Math.min(nextRetryCount - 1, 6))
                        );

                event.setNextAttemptAt(
                        LocalDateTime.now().plusSeconds(delaySeconds)
                );

                event.setStatus("NEW");

                outboxEventRepository.save(event);

                System.err.println(
                        "Failed to publish outbox event. " +
                                "eventId=" + event.getEventId() +
                                ", retryCount=" + nextRetryCount +
                                ", nextAttemptAt=" + event.getNextAttemptAt()
                );

                System.err.println(
                        "Reason: " + e.getMessage()
                );
            }
        }
    }
}

