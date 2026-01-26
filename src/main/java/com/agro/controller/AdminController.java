package com.agro.controller;

import com.agro.entity.Crop;
import com.agro.entity.Farmer;
import com.agro.entity.Order;
import com.agro.entity.User;
import com.agro.repository.CropRepository;
import com.agro.repository.OrderRepo;
import com.agro.repository.UserRepository;
import com.agro.service.CropService;
import com.agro.service.FarmerService;
import com.agro.service.RefundService;
import com.agro.service.impl.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FarmerService farmerService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final CropRepository cropRepository;
    private final OrderRepo orderRepo;
    
    @Autowired
    private RefundService refundService;

    public AdminController(FarmerService farmerService,
                           UserRepository userRepository,
                           CropRepository cropRepository,
                           PaymentService paymentService,
                           OrderRepo orderRepo) {
        this.farmerService = farmerService;
        this.userRepository = userRepository;
        this.cropRepository=cropRepository;
        this.paymentService=paymentService;
        this.orderRepo=orderRepo;
    }

    // GET ALL USERS
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/crops")
    public List<Crop> getAllCrops() {
    	return cropRepository.findAll();
    }

    // DELETE USER
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @PostMapping("/refund/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> refund(@PathVariable Long orderId) throws Exception {
        paymentService.refund(orderId);
        return ResponseEntity.ok("Refund successful");
    }
    
    
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    // ✅ APPROVE REFUND
    @PostMapping("/admin/orders/{orderId}/refund/approve")
    public ResponseEntity<?> approveRefund(
            @PathVariable Long orderId,
            Authentication auth) {

        refundService.approveRefund(orderId, auth.getName());
        return ResponseEntity.ok("Refund approved");
    }
   
}

