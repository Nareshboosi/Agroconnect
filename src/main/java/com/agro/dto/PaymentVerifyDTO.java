package com.agro.dto;

public class PaymentVerifyDTO {

    private String orderId;
    private String paymentId;
    private String signature;

    // ✅ REQUIRED: No-args constructor
    public PaymentVerifyDTO() {}

    // ===== Getters & Setters =====

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
