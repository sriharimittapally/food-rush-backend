package com.infosys.foodapp.repository;


import com.infosys.foodapp.entity.MenuItem;
import com.infosys.foodapp.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // Get full menu for a restaurant
    List<MenuItem> findByRestaurant(Restaurant restaurant);

    // Get only available items (what customer sees)
    List<MenuItem> findByRestaurantAndIsAvailableTrue(Restaurant restaurant);

    // Filter by category within a restaurant e.g. "Starters"
    List<MenuItem> findByRestaurantAndCategory(Restaurant restaurant, String category);

    // Veg-only filter
    List<MenuItem> findByRestaurantAndIsVegTrue(Restaurant restaurant);

    // Check if item belongs to restaurant (security check)
    boolean existsByIdAndRestaurant(Long id, Restaurant restaurant);
}