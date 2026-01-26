package com.agro.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.agro.entity.Crop;
import com.agro.entity.Order;
import com.agro.entity.OrderItem;
import com.agro.entity.RefundAudit;
import com.agro.enums.OrderStatus;
import com.agro.enums.PaymentStatus;
import com.agro.enums.RefundStatus;
import com.agro.repository.CropRepository;
import com.agro.repository.OrderRepo;
import com.agro.repository.RefundAuditRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.Refund;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private final OrderRepo orderRepo;
    private final CropRepository cropRepo;
    private final RefundAuditRepository refundAuditRepo;

    public PaymentService(OrderRepo orderRepo, RefundAuditRepository refundAuditRepo,CropRepository cropRepo) {
        this.orderRepo = orderRepo;
        this.refundAuditRepo = refundAuditRepo;
        this.cropRepo=cropRepo;
    }

    // ================= CREATE PAYMENT =================
    public Map<String, Object> createPayment(Long orderId) throws Exception {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        RazorpayClient client = new RazorpayClient(razorpayKey, razorpayKeySecret);

        JSONObject options = new JSONObject();
        options.put("amount", order.getTotalPrice().intValue() * 100);
        options.put("currency", "INR");
        options.put("receipt", "order_" + order.getId());

        com.razorpay.Order rpOrder = client.orders.create(options);

        order.setRazorpayOrderId(rpOrder.get("id"));
        orderRepo.save(order);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", rpOrder.get("id"));
        response.put("amount", order.getTotalPrice());
        response.put("key", razorpayKey);

        return response;
    }

    // ================= VERIFY & MARK PAID =================
    @Transactional
    public void markOrderAsPaid(String razorpayOrderId, String paymentId) {

        Order order = orderRepo.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔥 REDUCE STOCK HERE (ONCE, SAFELY)
        for (OrderItem item : order.getItems()) {
            Crop crop = item.getCrop();

            int remaining = crop.getAvailableQuantity() - item.getQuantity();
            if (remaining < 0) {
                throw new RuntimeException("Out of stock: " + crop.getCropName());
            }

            crop.setAvailableQuantity(remaining);
            cropRepo.save(crop);
        }

        order.markPaid(paymentId, razorpayOrderId);
        orderRepo.save(order);
    }



    // ================= VERIFY SIGNATURE =================
    public boolean verifySignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature) {

        try {
            String payload = razorpayOrderId + "|" + razorpayPaymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey =
                    new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);

            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = Hex.encodeHexString(digest);

            return generatedSignature.equals(razorpaySignature);

        } catch (Exception e) {
            return false;
        }
    }

    // ================= ADMIN REFUND =================
    @Transactional
    public void processRefund(Long orderId, String adminEmail) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.isPaid())
            throw new RuntimeException("Order not paid");

        if (order.getRefundStatus() == RefundStatus.APPROVED
                || order.getRefundStatus() == RefundStatus.COMPLETED)
            throw new RuntimeException("Already refunded");

        try {
            RazorpayClient client = new RazorpayClient(razorpayKey, razorpayKeySecret);

            JSONObject request = new JSONObject();
            request.put("amount", order.getTotalPrice().intValue() * 100);

            Refund refund = client.payments.refund(
                    order.getRazorpayPaymentId(), request);

            order.approveRefund(refund.get("id"));
            order.completeRefund();
            orderRepo.save(order);

            RefundAudit audit = new RefundAudit();
            audit.setOrderId(order.getId());
            audit.setPaymentId(order.getRazorpayPaymentId());
            audit.setAmount(order.getTotalPrice());
            audit.setApprovedBy(adminEmail);
            audit.setApprovedAt(LocalDateTime.now());

            refundAuditRepo.save(audit);

        } catch (Exception e) {
            throw new RuntimeException("Refund failed: " + e.getMessage());
        }
    }

    public void requestRefund(Long orderId, String buyerEmail) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔒 Ownership check
        if (!order.getBuyer().getEmail().equals(buyerEmail)) {
            throw new RuntimeException("Unauthorized refund request");
        }

        // 💰 Must be paid
        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Order not paid");
        }

        // 🔁 Already requested / completed
        if (order.getRefundStatus() != RefundStatus.NONE) {
            throw new RuntimeException("Refund already requested or processed");
        }

        // 🚫 Delivered orders
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Delivered orders cannot be refunded");
        }

        // ✅ Request refund
        order.requestRefund();
        orderRepo.save(order);
    }

    @Transactional
    public void refund(Long orderId) {

        // 1️⃣ Fetch order
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 2️⃣ Validate payment
        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Order is not paid");
        }

        // 3️⃣ Validate refund state
        if (order.getRefundStatus() == RefundStatus.COMPLETED) {
            throw new RuntimeException("Refund already completed");
        }

        if (order.getRefundStatus() != RefundStatus.APPROVED) {
            throw new RuntimeException("Refund not approved by admin");
        }

        // 4️⃣ Razorpay refund
        try {
            RazorpayClient client =
                    new RazorpayClient(razorpayKey, razorpayKeySecret);

            JSONObject request = new JSONObject();
            request.put("amount", order.getTotalPrice().intValue() * 100); // paise

            Refund refund = client.payments.refund(
                    order.getRazorpayPaymentId(),
                    request
            );

            // 5️⃣ Update order (IMPORTANT)
            order.completeRefund();               // sets paymentStatus + refundStatus + refundedAt
            order.setRefundId(refund.get("id"));

            orderRepo.save(order);

        } catch (Exception e) {
            throw new RuntimeException("Refund failed: " + e.getMessage());
        }
    }
    
    @Transactional
    public void cancelRefundRequest(Long orderId, String adminEmail) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔒 Refund must be in REQUESTED state
        if (order.getRefundStatus() != RefundStatus.REQUESTED) {
            throw new RuntimeException("Refund request cannot be cancelled");
        }

        // 🚫 Safety checks
        if (order.getRefundStatus() == RefundStatus.APPROVED ||
            order.getRefundStatus() == RefundStatus.COMPLETED) {
            throw new RuntimeException("Refund already processed");
        }

        // ✅ Cancel refund request
        order.setRefundStatus(RefundStatus.NONE);
        order.setRefundRequestedAt(null); // if you track this
        orderRepo.save(order);

        // 📝 OPTIONAL: Audit log (recommended)
        RefundAudit audit = new RefundAudit();
        audit.setOrderId(order.getId());
        audit.setAmount(order.getTotalPrice());
        audit.setApprovedBy(adminEmail);
        audit.setApprovedAt(LocalDateTime.now());
       

        refundAuditRepo.save(audit);
    }


}
