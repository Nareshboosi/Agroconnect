package com.agro.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.agro.enums.OrderStatus;
import com.agro.enums.PaymentStatus;
import com.agro.enums.RefundStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------- RELATIONS ----------------
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    // ---------------- ORDER ----------------
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private OrderStatus status;

    private Double totalPrice;
    private LocalDate orderDate;

    private boolean reordered = false;
    private boolean buyerConfirmed = false;

    // ---------------- PAYMENT ----------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private LocalDateTime paidAt;

    // ---------------- REFUND ----------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus refundStatus = RefundStatus.NONE;

    private String refundId;
    private LocalDateTime refundRequestedAt;
    private LocalDateTime refundedAt;

    // ---------------- CONSTRUCTORS ----------------
    public Order() {}

    // ---------------- GETTERS & SETTERS ----------------

    public Long getId() {
        return id;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public User getBuyer() {
        return buyer;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public boolean isReordered() {
        return reordered;
    }

    public boolean isBuyerConfirmed() {
        return buyerConfirmed;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public RefundStatus getRefundStatus() {
        return refundStatus;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public String getRefundId() {
        return refundId;
    }

    public LocalDateTime getRefundRequestedAt() {
        return refundRequestedAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    // ---------------- SETTERS ----------------

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setReordered(boolean reordered) {
        this.reordered = reordered;
    }

    public void setBuyerConfirmed(boolean buyerConfirmed) {
        this.buyerConfirmed = buyerConfirmed;
    }

    public void markPaid(String paymentId, String razorpayOrderId) {
        this.paymentStatus = PaymentStatus.PAID;
        this.razorpayPaymentId = paymentId;
        this.razorpayOrderId = razorpayOrderId;
        this.paidAt = LocalDateTime.now();
    }

    public void requestRefund() {
        this.refundStatus = RefundStatus.REQUESTED;
        this.refundRequestedAt = LocalDateTime.now();
    }

    public void approveRefund(String refundId) {
        this.refundStatus = RefundStatus.APPROVED;
        this.refundId = refundId;
    }

    public void completeRefund() {
        this.paymentStatus = PaymentStatus.REFUNDED;
        this.refundStatus = RefundStatus.COMPLETED;
        this.refundedAt = LocalDateTime.now();
    }

	public void setRefundStatus(RefundStatus refundStatus) {
		// TODO Auto-generated method stub
		this.refundStatus=refundStatus;
		
	}

	public void setRefundRequestedAt(LocalDateTime refundRequestedAt) {
		// TODO Auto-generated method stub
		this.refundRequestedAt=refundRequestedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public void setRazorpayPaymentId(String razorpayPaymentId) {
		this.razorpayPaymentId = razorpayPaymentId;
	}

	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public void setRefundedAt(LocalDateTime refundedAt) {
		this.refundedAt = refundedAt;
	}

	public boolean isPaid() {
		// TODO Auto-generated method stub
		return this.paymentStatus==PaymentStatus.PAID;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		// TODO Auto-generated method stub
		this.paymentStatus=paymentStatus;
	}
}
