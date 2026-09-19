package com.eventcommerce.contracts.events;

public class InventoryReservedEvent {

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    private String eventId;
    private Long orderId;
    private String productId;
    private Integer quantity;

    public InventoryReservedEvent() {
    }

    public InventoryReservedEvent(
            String eventId,
            Long orderId,
            String productId,
            Integer quantity) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
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
}
