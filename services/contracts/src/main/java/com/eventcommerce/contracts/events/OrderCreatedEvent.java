package com.eventcommerce.contracts.events;

import java.math.BigDecimal;

public class OrderCreatedEvent {

    private Long orderId;
    private String customerId;
    private String productId;
    private Integer quantity;
    private BigDecimal amount;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(
            Long orderId,
            String customerId,
            String productId,
            Integer quantity,
            BigDecimal amount) {

        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}