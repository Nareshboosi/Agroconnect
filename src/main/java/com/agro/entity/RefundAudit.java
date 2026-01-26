package com.agro.entity;

import java.time.LocalDateTime;

import com.agro.enums.RefundStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RefundAudit {

    public RefundAudit() {
		super();
	}
	public RefundAudit(Long id, Long orderId, Double amount, String approvedBy, LocalDateTime approvedAt) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.amount = amount;
		this.approvedBy = approvedBy;
		this.approvedAt = approvedAt;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}
	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}
	
	
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	
	
	  

	    private Long orderId;
	    private String buyerEmail;
	    private String adminEmail;

	    private Double amount;

	    @Enumerated(EnumType.STRING)
	    private RefundStatus status;

	    private LocalDateTime actionAt;

	    public static RefundAudit createRequest(Order order, String buyer) {
	        RefundAudit r = new RefundAudit();
	        r.orderId = order.getId();
	        r.buyerEmail = buyer;
	        r.amount = order.getTotalPrice();
	        r.status = RefundStatus.REQUESTED;
	        r.actionAt = LocalDateTime.now();
	        return r;
	    }

	    public static RefundAudit approve(Order order, String admin) {
	        RefundAudit r = new RefundAudit();
	        r.orderId = order.getId();
	        r.adminEmail = admin;
	        r.amount = order.getTotalPrice();
	        r.status = RefundStatus.APPROVED;
	        r.actionAt = LocalDateTime.now();
	        return r;
	    }
	



	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String approvedBy; // admin email
    private LocalDateTime approvedAt;
   
    private String paymentId;
    
    

}
