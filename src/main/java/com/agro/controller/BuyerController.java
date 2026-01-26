package com.agro.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Authentication;
import org.springframework.http.ResponseEntity;
//import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agro.entity.Crop;
import com.agro.entity.Order;
import com.agro.entity.User;
import com.agro.repository.CropRepository;
import com.agro.repository.OrderRepo;
import com.agro.repository.UserRepository;
import com.agro.service.RefundService;

@RestController
@RequestMapping("/api/buyer")
@CrossOrigin
public class BuyerController {
	 @Autowired
	    private OrderRepo orderRepo;
	 
	 @Autowired
	 private RefundService refundService;

	    @Autowired
	    private UserRepository userRepository;

	    @PreAuthorize("hasRole('BUYER')")
	    @GetMapping("/orders")
	    public List<Order> getBuyerOrders(Principal principal) {

	        User buyer = userRepository
	                .findByEmail(principal.getName())
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        return orderRepo.findByBuyerEmail(buyer.getEmail());
	    }
	    
	    
	    @PostMapping("/orders/{orderId}/refund")
	    public ResponseEntity<?> requestRefund(
	            @PathVariable Long orderId,
	            org.springframework.security.core.Authentication auth) {

	        refundService.requestRefund(orderId, auth.getName());
	        return ResponseEntity.ok("Refund requested");
	    }

}