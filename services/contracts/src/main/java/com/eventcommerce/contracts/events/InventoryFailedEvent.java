package com.eventcommerce.contracts.events;

public class InventoryFailedEvent {

    private Long orderId;
    private String productId;
    private Integer quantity;
    private String reason;

    public InventoryFailedEvent() {
    }

    public InventoryFailedEvent(
            Long orderId,
            String productId,
            Integer quantity,
            String reason) {

        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.reason = reason;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}