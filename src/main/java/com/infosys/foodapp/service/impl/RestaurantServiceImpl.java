package com.infosys.foodapp.service.impl;

import com.infosys.foodapp.dto.request.RestaurantRequest;
import com.infosys.foodapp.dto.response.RestaurantResponse;
import com.infosys.foodapp.entity.Restaurant;
import com.infosys.foodapp.entity.User;
import com.infosys.foodapp.repository.RestaurantRepository;
import com.infosys.foodapp.repository.UserRepository;
import com.infosys.foodapp.service.RestaurantService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    // ─── Helper: get user by email ────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ─── Helper: get restaurant by id ────────────────────────

    private Restaurant getRestaurantEntityById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found with id: " + id));
    }


    public RestaurantResponse getMyRestaurant(String ownerEmail) {
        Restaurant restaurant = restaurantRepository
                .findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("No restaurant found"));
        return mapToResponse(restaurant);
    }
    // ─── Helper: Entity → Response DTO ───────────────────────

    private RestaurantResponse mapToResponse(Restaurant r) {
        return RestaurantResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .address(r.getAddress())
                .city(r.getCity())
                .phone(r.getPhone())
                .imageUrl(r.getImageUrl())
                .cuisineType(r.getCuisineType())
                .rating(r.getRating())
                .isOpen(r.isOpen())
                .ownerName(r.getOwner().getFullName())
                .ownerEmail(r.getOwner().getEmail())
                .createdAt(r.getCreatedAt())
                .build();
    }

    // ─── Create Restaurant ────────────────────────────────────

    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest request,
                                               String ownerEmail) {
        User owner = getUserByEmail(ownerEmail);

        // One owner can only have one restaurant
        if (restaurantRepository.existsByOwner(owner)) {
            throw new RuntimeException(
                    "You already have a registered restaurant");
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setPhone(request.getPhone());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setOwner(owner);
        restaurant.setOpen(true);
        restaurant.setRating(0.0);

        return mapToResponse(restaurantRepository.save(restaurant));
    }

    // ─── Update Restaurant ────────────────────────────────────

    @Override
    public RestaurantResponse updateRestaurant(RestaurantRequest request,
                                               String ownerEmail) {
        User owner = getUserByEmail(ownerEmail);

        Restaurant restaurant = restaurantRepository.findByOwner(owner)
                .orElseThrow(() ->
                        new RuntimeException("No restaurant found for this owner"));

        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setPhone(request.getPhone());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setCuisineType(request.getCuisineType());

        return mapToResponse(restaurantRepository.save(restaurant));
    }

    // ─── Toggle Open/Close ────────────────────────────────────

    @Override
    public RestaurantResponse toggleOpenStatus(String ownerEmail) {
        User owner = getUserByEmail(ownerEmail);

        Restaurant restaurant = restaurantRepository.findByOwner(owner)
                .orElseThrow(() ->
                        new RuntimeException("No restaurant found for this owner"));

        // Flip the status
        restaurant.setOpen(!restaurant.isOpen());

        return mapToResponse(restaurantRepository.save(restaurant));
    }

    // ─── Get All Restaurants (Public) ────────────────────────

    @Override
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .filter(r -> !r.isDeleted())   // ← exclude deleted
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Get By ID (Public) ───────────────────────────────────

    @Override
    public RestaurantResponse getRestaurantById(Long id) {
        return mapToResponse(getRestaurantEntityById(id));
    }

    // ─── Search (Public) ──────────────────────────────────────

    @Override
    public List<RestaurantResponse> searchRestaurants(String keyword) {
        return restaurantRepository.searchByKeyword(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Get By City (Public) ─────────────────────────────────

    @Override
    public List<RestaurantResponse> getRestaurantsByCity(String city) {
        return restaurantRepository.findByCityAndIsOpenTrue(city)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Delete (Admin) ───────────────────────────────────────

    @Override
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // soft delete
        restaurant.setDeleted(true);
        restaurant.setOpen(false);
        restaurantRepository.save(restaurant);
    }
}