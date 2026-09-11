package com.eventcommerce.contracts.events;

public class InventoryReservedEvent {

    private Long orderId;
    private String productId;
    private Integer quantity;

    public InventoryReservedEvent() {
    }

    public InventoryReservedEvent(
            Long orderId,
            String productId,
            Integer quantity) {
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
