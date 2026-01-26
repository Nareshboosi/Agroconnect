package com.agro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agro.entity.RefundAudit;
import com.agro.repository.RefundAuditRepository;
import com.agro.service.impl.PaymentService;

@RestController
@RequestMapping("/api/admin/refunds")
public class AdminRefundController {

    private final PaymentService paymentService;
    private final RefundAuditRepository repository;

  

    public AdminRefundController(PaymentService paymentService,RefundAuditRepository repository) {
        this.paymentService = paymentService;
        this.repository = repository;
    }

    @PostMapping("/{orderId}/approve")
    public ResponseEntity<?> approveRefund(
            @PathVariable Long orderId,
            Authentication auth
    ) {
        paymentService.processRefund(orderId, auth.getName());
        return ResponseEntity.ok("Refund successful");
    }
    
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelRefund(
            @PathVariable Long orderId,
            Authentication auth
    ) {
        paymentService.cancelRefundRequest(orderId, auth.getName());
        return ResponseEntity.ok("Refund request cancelled");
    }

   

    @GetMapping
    public List<RefundAudit> getAll() {
        return repository.findAll();
    }
}
