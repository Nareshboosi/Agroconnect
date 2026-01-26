package com.agro.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agro.entity.Order;
import com.agro.repository.OrderRepo;

@RestController
@RequestMapping("/api/admin/analytics")
public class AdminAnalyticsController {

    private final OrderRepo orderRepository;

    public AdminAnalyticsController(OrderRepo orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public Map<String, Object> analytics() {

        List<Order> orders = orderRepository.findAll();

        double revenue = orders.stream()
                .filter(o -> "PAID".equals(o.getPaymentStatus()))
                .mapToDouble(Order::getTotalPrice)
                .sum();

        double refunded = orders.stream()
                .filter(o -> "REFUNDED".equals(o.getPaymentStatus()))
                .mapToDouble(Order::getTotalPrice)
                .sum();

        Map<String, Object> data = new HashMap<>();
        data.put("revenue", revenue);
        data.put("refunds", refunded);
        data.put("netRevenue", revenue - refunded);

        return data;
    }
}
