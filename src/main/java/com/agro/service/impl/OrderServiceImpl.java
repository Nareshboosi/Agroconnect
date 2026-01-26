package com.agro.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agro.dto.OrderItemRequest;
import com.agro.entity.*;
import com.agro.enums.OrderStatus;
import com.agro.enums.RefundStatus;
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

    // ================= PLACE ORDER =================
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

    // ================= BUYER ORDERS =================
    @Override
    public List<Order> getBuyerOrders(String buyerEmail) {
        return orderRepo.findByBuyerEmail(buyerEmail);
    }

    // ================= FARMER ORDERS =================
    @Override
    public List<Order> getFarmerOrders(String farmerEmail) {

        Farmer farmer = farmerRepo.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        return orderRepo.findOrdersForFarmer(farmer.getId());
    }

    // ================= UPDATE STATUS =================
    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔐 Prevent duplicate confirm
        if (status == OrderStatus.CONFIRMED && order.getStatus() == OrderStatus.CONFIRMED) {
            return order;
        }

        // ✅ Deduct stock ONLY when farmer confirms
//        if (status == OrderStatus.CONFIRMED) {
//            for (OrderItem item : order.getItems()) {
//                Crop crop = item.getCrop();
//
//                if (crop.getAvailableQuantity() < item.getQuantity()) {
//                    throw new RuntimeException(
//                        "Out of stock: " + crop.getCropName()
//                    );
//                }
//
//                crop.setAvailableQuantity(
//                    crop.getAvailableQuantity() - item.getQuantity()
//                );
//                cropRepo.save(crop);
//            }
//        }

        order.setStatus(status);
        return orderRepo.save(order);
    }


    // ================= REQUEST REFUND =================
    public void requestRefund(Long orderId, String email) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getBuyer().getEmail().equals(email))
            throw new RuntimeException("Unauthorized");

        if (!order.isPaid())
            throw new RuntimeException("Order not paid");

        if (order.getRefundStatus() != RefundStatus.NONE)
            throw new RuntimeException("Refund already requested");

        order.requestRefund();
        orderRepo.save(order);
    }



    // ================= CONFIRM DELIVERY =================
    public void confirmDelivery(Long orderId, String buyerEmail) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getBuyer().getEmail().equals(buyerEmail))
            throw new RuntimeException("Unauthorized");

        order.setBuyerConfirmed(true);
        orderRepo.save(order);
    }

    // ================= REORDER =================
    @Override
    public Order reorder(Long orderId, String buyerEmail) {

        Order oldOrder = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!oldOrder.getBuyer().getEmail().equals(buyerEmail))
            throw new RuntimeException("Unauthorized");

        if (oldOrder.isReordered())
            throw new RuntimeException("Already reordered");

        if (oldOrder.getStatus() != OrderStatus.CANCELLED)
            throw new RuntimeException("Only cancelled orders can be reordered");

        Order newOrder = new Order();
        newOrder.setBuyer(oldOrder.getBuyer());
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setOrderDate(LocalDate.now());

        List<OrderItem> items = new ArrayList<>();
        double total = 0;

        for (OrderItem oldItem : oldOrder.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(newOrder);
            item.setCrop(oldItem.getCrop());
            item.setQuantity(oldItem.getQuantity());

            double price = oldItem.getCrop().getPrice() * oldItem.getQuantity();
            item.setPrice(price);

            total += price;
            items.add(item);
        }

        newOrder.setItems(items);
        newOrder.setTotalPrice(total);

        oldOrder.setReordered(true);
        orderRepo.save(oldOrder);

        return orderRepo.save(newOrder);
    }
}
