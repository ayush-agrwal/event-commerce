package com.eventcommerce.contracts.events;

import java.math.BigDecimal;

public class PaymentRefundedEvent {

    private String eventId;
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public PaymentRefundedEvent() {
    }

    public PaymentRefundedEvent(
            String eventId
            ,Long paymentId,
            Long orderId,
            BigDecimal amount) {
    this.eventId = eventId;
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}