package com.agro.controller;

import com.agro.service.OrderService;
import com.agro.service.impl.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService,OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService=orderService;
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Map<String, Object>> pay(
            @PathVariable Long orderId,
            Authentication auth) throws Exception {

        System.out.println("PAY CALLED BY: " + auth.getName());
        System.out.println("ROLES: " + auth.getAuthorities());
        

        return ResponseEntity.ok(paymentService.createPayment(orderId));
    }
    
    
    
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload) {

        String razorpayOrderId = payload.get("razorpay_order_id");
        String razorpayPaymentId = payload.get("razorpay_payment_id");
        String razorpaySignature = payload.get("razorpay_signature");

        if (razorpayOrderId == null || razorpayPaymentId == null || razorpaySignature == null) {
            return ResponseEntity.badRequest().body("Invalid payment payload");
        }

        boolean isValid = paymentService.verifySignature(
                razorpayOrderId,
                razorpayPaymentId,
                razorpaySignature
        );

        if (!isValid) {
            return ResponseEntity.badRequest().body("Payment verification failed");
        }

        // ✅ mark order as PAID
        paymentService.markOrderAsPaid(razorpayOrderId, razorpayPaymentId);

        return ResponseEntity.ok().build();
    }

    
    
    @PutMapping("/{orderId}/confirm-delivery")
    public ResponseEntity<?> confirmDelivery(
            @PathVariable Long orderId,
            Authentication auth
    ) {
        orderService.confirmDelivery(orderId, auth.getName());
        return ResponseEntity.ok("Delivery confirmed");
    }

}
