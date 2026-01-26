package com.agro.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agro.entity.Order;
import com.agro.entity.RefundAudit;
import com.agro.enums.RefundStatus;
import com.agro.repository.OrderRepo;
import com.agro.repository.RefundAuditRepository;
import com.agro.service.RefundService;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class RefundServiceImpl implements RefundService {

    private final RefundAuditRepository refundAuditRepository;
	
	 @Autowired
	    private OrderRepo orderRepo;


    RefundServiceImpl(RefundAuditRepository refundAuditRepository) {
        this.refundAuditRepository = refundAuditRepository;
    }


	@Transactional
	public void requestRefund(Long orderId, String buyerEmail) {

	    Order order = orderRepo.findById(orderId)
	        .orElseThrow(() -> new RuntimeException("Order not found"));

	    if (!order.isPaid()) {
	        throw new RuntimeException("Unpaid order cannot be refunded");
	    }

	    order.setRefundStatus(RefundStatus.REQUESTED);
	    order.setRefundRequestedAt(LocalDateTime.now());

	    orderRepo.save(order);

	    refundAuditRepository.save(
	        RefundAudit.createRequest(order, buyerEmail)
	    );
	}

	
	@Transactional
	public void approveRefund(Long orderId, String adminEmail) {

	    Order order = orderRepo.findById(orderId)
	        .orElseThrow();

	    order.setRefundStatus(RefundStatus.APPROVED);

	    orderRepo.save(order);

	    refundAuditRepository.save(
	        RefundAudit.approve(order, adminEmail)
	    );
	}
}
