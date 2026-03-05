package com.infosys.foodapp.repository;



import com.infosys.foodapp.entity.Order;
import com.infosys.foodapp.entity.Restaurant;
import com.infosys.foodapp.entity.User;
import com.infosys.foodapp.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Customer: view my orders (newest first)
    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);

    // Restaurant: view incoming orders
    List<Order> findByRestaurantOrderByCreatedAtDesc(Restaurant restaurant);

    // Restaurant: filter by status e.g. all PENDING orders
    List<Order> findByRestaurantAndStatus(Restaurant restaurant, OrderStatus status);

    // Customer: filter their orders by status
    List<Order> findByCustomerAndStatus(User customer, OrderStatus status);

    // Admin: revenue report — sum total for a restaurant
    @Query("SELECT SUM(o.totalAmount) FROM Order o " +
            "WHERE o.restaurant = :restaurant AND o.status = 'DELIVERED'")
    Double getTotalRevenueByRestaurant(@Param("restaurant") Restaurant restaurant);

    // Admin: count orders by status
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);
}
