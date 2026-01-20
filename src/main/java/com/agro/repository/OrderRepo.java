package com.agro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.agro.entity.Order;

public interface OrderRepo extends JpaRepository<Order, Long> {

    // BUYER ORDERS
    List<Order> findByBuyerEmail(String email);

    // FARMER ORDERS
    @Query("""
        SELECT DISTINCT o FROM Order o
        JOIN o.items i
        JOIN i.crop c
        WHERE c.farmer.id = :farmerId
    """)
    List<Order> findOrdersForFarmer(@Param("farmerId") Long farmerId);
}
