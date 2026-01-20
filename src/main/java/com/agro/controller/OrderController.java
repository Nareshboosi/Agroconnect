package com.agro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.agro.dto.OrderItemRequest;
import com.agro.entity.Farmer;
import com.agro.entity.Order;
import com.agro.enums.OrderStatus;
import com.agro.repository.FarmerRepository;
import com.agro.repository.OrderRepo;
import com.agro.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private FarmerRepository farmerRepo;

    @PostMapping("/place")
    public Order placeOrder(
            Authentication authentication,
            @RequestBody List<OrderItemRequest> items
    ) {
        return orderService.placeOrder(authentication.getName(), items);
    }

    @GetMapping("/buyer")
    public List<Order> getBuyerOrders(Authentication auth) {
        return orderService.getBuyerOrders(auth.getName());
    }

//    @GetMapping("/farmer")
//    public List<Order> getFarmerOrders(Authentication auth) {
//    	 String email = auth.getName();
//    	    Farmer farmer = farmerRepo.findByEmail(email)
//    	            .orElseThrow();
//
//    	    System.out.println("LOGGED FARMER ID = " + farmer.getId());
//
//        return orderService.getFarmerOrders(auth.getName());
//    }

    @GetMapping("/farmer")
    public List<Order> getFarmerOrders(Authentication auth) {
        String email = auth.getName();
        Farmer farmer = farmerRepo.findByEmail(email)
                .orElseThrow();

        System.out.println("LOGGED FARMER ID = " + farmer.getId());

        return orderRepo.findOrdersForFarmer(farmer.getId());
    }

    
    @PutMapping("/{orderId}/cancel")
    public Order cancelOrder(@PathVariable Long orderId, Authentication auth) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED ||
            order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Order cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepo.save(order);
    }
    
    @GetMapping("/admin")
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }
    
    @PostMapping("/{orderId}/reorder")
    public ResponseEntity<Order> reorder(
            @PathVariable Long orderId,
            Authentication auth
    ) {
        String email = auth.getName();
        Order newOrder = orderService.reorder(orderId, email);

        // ✅ Always return 200 + body
        return ResponseEntity.ok(newOrder);
    }
    
    @PutMapping("/{orderId}/status")
    public Order updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status,
            Authentication auth
    ) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String role = auth.getAuthorities().iterator().next().getAuthority();

        // BUYER can only cancel
        if (role.equals("ROLE_BUYER") && status != OrderStatus.CANCELLED) {
            throw new RuntimeException("Buyer not allowed to set this status");
        }

        // FARMER cannot cancel
        if (role.equals("ROLE_FARMER") && status == OrderStatus.CANCELLED) {
            throw new RuntimeException("Farmer cannot cancel orders");
        }

        order.setStatus(status);
        return orderRepo.save(order);
    }


}
