package com.agro.dto;

import java.util.List;

public class OrderRequest {

    private Long buyerId;
    private List<Long> cropIds;
    private double totalAmount;

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public List<Long> getCropIds() {
        return cropIds;
    }

    public void setCropIds(List<Long> cropIds) {
        this.cropIds = cropIds;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
