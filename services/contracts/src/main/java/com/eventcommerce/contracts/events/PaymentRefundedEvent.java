package com.eventcommerce.contracts.events;

import java.math.BigDecimal;

public class PaymentRefundedEvent {

    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;

    public PaymentRefundedEvent() {
    }

    public PaymentRefundedEvent(
            Long paymentId,
            Long orderId,
            BigDecimal amount) {

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