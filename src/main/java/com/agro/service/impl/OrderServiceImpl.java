package com.agro.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agro.dto.OrderItemRequest;
import com.agro.entity.*;
import com.agro.enums.OrderStatus;
import com.agro.repository.*;
import com.agro.service.OrderService;

import jakarta.transaction.Transactional;


@Transactional
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private CropRepository cropRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private FarmerRepository farmerRepo;

    @Override
    public Order placeOrder(String buyerEmail, List<OrderItemRequest> items) {

        User buyer = userRepo.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Order order = new Order();
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (OrderItemRequest req : items) {
            Crop crop = cropRepo.findById(req.getCropId())
                    .orElseThrow(() -> new RuntimeException("Crop not found"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setCrop(crop);
            item.setQuantity(req.getQuantity());
            item.setPrice(crop.getPrice() * req.getQuantity());

            total += item.getPrice();
            orderItems.add(item);
        }

        order.setItems(orderItems);
        order.setTotalPrice(total);

        return orderRepo.save(order);
    }

    @Override
    public List<Order> getBuyerOrders(String buyerEmail) {
        return orderRepo.findByBuyerEmail(buyerEmail);
    }

    @Override
    public List<Order> getFarmerOrders(String farmerEmail) {
        Farmer farmer = farmerRepo.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        return orderRepo.findOrdersForFarmer(farmer.getId());
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ ONLY reduce stock when farmer CONFIRMS
        if (status == OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.CONFIRMED) {

            for (OrderItem item : order.getItems()) {
                Crop crop = item.getCrop();

                int remainingQty = crop.getAvailableQuantity() - item.getQuantity();
                if (remainingQty < 0) {
                    throw new RuntimeException("Insufficient stock for " + crop.getCropName());
                }

                crop.setAvailableQuantity(remainingQty);
                cropRepo.save(crop);
            }
        }

        order.setStatus(status);
        return orderRepo.save(order);
    }


    @Override
    public Order reorder(Long orderId, String buyerEmail) {

        Order oldOrder = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (oldOrder.isReordered()) {
            throw new RuntimeException("Order already reordered");
        }

        if (oldOrder.getStatus() != OrderStatus.CANCELLED) {
            throw new RuntimeException("Only cancelled orders can be reordered");
        }

        Order newOrder = new Order();
        newOrder.setBuyer(oldOrder.getBuyer());
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setOrderDate(LocalDate.now());

        List<OrderItem> newItems = new ArrayList<>();
        double total = 0;

        for (OrderItem oldItem : oldOrder.getItems()) {

            OrderItem item = new OrderItem();
            item.setOrder(newOrder); // 🔥 VERY IMPORTANT
            item.setCrop(oldItem.getCrop());
            item.setQuantity(oldItem.getQuantity());

            double price = oldItem.getCrop().getPrice() * oldItem.getQuantity();
            item.setPrice(price);

            total += price;
            newItems.add(item);
        }

        newOrder.setItems(newItems);
        newOrder.setTotalPrice(total);

        // mark old order
        oldOrder.setReordered(true);
        orderRepo.save(oldOrder);

        // ✅ cascade saves items
        return orderRepo.save(newOrder);
    }




}
