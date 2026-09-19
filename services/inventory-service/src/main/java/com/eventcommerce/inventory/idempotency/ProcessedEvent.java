package com.eventcommerce.inventory.idempotency;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {

    @Id
    private String eventId;

    @Column(nullable = false)
    private String consumerName;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    // Default constructor (required by JPA)
    public ProcessedEvent() {
    }

    // Parameterized constructor
    public ProcessedEvent(
            String eventId,
            String consumerName,
            LocalDateTime processedAt) {

        this.eventId = eventId;
        this.consumerName = consumerName;
        this.processedAt = processedAt;
    }

    // Getters and Setters

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}