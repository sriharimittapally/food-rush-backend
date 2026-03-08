package com.infosys.foodapp.repository;


import com.infosys.foodapp.entity.Restaurant;
import com.infosys.foodapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // Owner can fetch their own restaurant
    Optional<Restaurant> findByOwner(User owner);

    Optional<Restaurant> findByOwnerEmail(String email);

    List<Restaurant> findByDeletedFalse();
    List<Restaurant> findByDeletedFalseAndOpenTrue();

    // Check if owner already has a restaurant
    boolean existsByOwner(User owner);

    // Search restaurants by name or city (for search bar)
    @Query("SELECT r FROM Restaurant r WHERE " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Restaurant> searchByKeyword(@Param("keyword") String keyword);

    // Get all open restaurants in a city
    List<Restaurant> findByCityAndIsOpenTrue(String city);

    // Filter by cuisine type
    List<Restaurant> findByCuisineTypeContainingIgnoreCase(String cuisineType);
}
