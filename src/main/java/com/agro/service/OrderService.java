package com.agro.service;

import java.util.List;

import com.agro.dto.OrderItemRequest;
import com.agro.entity.Order;
import com.agro.enums.OrderStatus;

public interface OrderService {

    Order placeOrder(String buyerEmail, List<OrderItemRequest> items);

    List<Order> getBuyerOrders(String buyerEmail);

    List<Order> getFarmerOrders(String farmerEmail);

    Order updateOrderStatus(Long orderId, OrderStatus status);

	Order reorder(Long orderId, String buyerEmail);
}
