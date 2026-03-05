package com.infosys.foodapp.repository;

import com.infosys.foodapp.entity.Order;
import com.infosys.foodapp.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Get all items for a specific order
    List<OrderItem> findByOrder(Order order);
}