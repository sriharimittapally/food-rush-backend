package com.infosys.foodapp.service;

import com.infosys.foodapp.dto.request.RestaurantRequest;
import com.infosys.foodapp.dto.response.RestaurantResponse;

import java.util.List;

public interface RestaurantService {

    // Owner
    RestaurantResponse createRestaurant(RestaurantRequest request, String ownerEmail);
    RestaurantResponse updateRestaurant(RestaurantRequest request, String ownerEmail);
    RestaurantResponse toggleOpenStatus(String ownerEmail);
     RestaurantResponse getMyRestaurant(String ownerEmail);
    // Public
    List<RestaurantResponse> getAllRestaurants();
    RestaurantResponse getRestaurantById(Long id);
    List<RestaurantResponse> searchRestaurants(String keyword);
    List<RestaurantResponse> getRestaurantsByCity(String city);

    // Admin
    void deleteRestaurant(Long id);
}