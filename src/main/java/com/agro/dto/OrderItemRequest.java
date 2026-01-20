package com.agro.dto;

public class OrderItemRequest {

    private Long cropId;
    private int quantity;

    public OrderItemRequest() {}

    public Long getCropId() {
        return cropId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setCropId(Long cropId) {
        this.cropId = cropId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
