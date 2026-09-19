package com.eventcommerce.contracts.events;

import java.math.BigDecimal;

public class PaymentSucceededEvent {

    private String eventId;
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private String productId;
    private Integer quantity;

    public PaymentSucceededEvent() {
    }

    public PaymentSucceededEvent(
            String eventId,
            Long paymentId,
            Long orderId,
            BigDecimal amount,
            String productId,
            Integer quantity) {

        this.eventId = eventId;
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}