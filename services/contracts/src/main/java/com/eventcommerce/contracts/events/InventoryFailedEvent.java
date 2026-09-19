package com.eventcommerce.contracts.events;

public class InventoryFailedEvent {

    private String eventId;
    private Long orderId;
    private String productId;
    private Integer quantity;
    private String reason;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public InventoryFailedEvent() {
    }

    public InventoryFailedEvent(
            String eventId
            ,Long orderId,
            String productId,
            Integer quantity,
            String reason) {
        this.eventId = eventId;
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