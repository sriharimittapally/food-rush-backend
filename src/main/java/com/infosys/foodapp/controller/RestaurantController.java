package com.infosys.foodapp.controller;

import com.infosys.foodapp.dto.request.RestaurantRequest;
import com.infosys.foodapp.dto.response.RestaurantResponse;
import com.infosys.foodapp.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Restaurants", description = "Restaurant management APIs")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // ════════════════════════════════════════════════
    // PUBLIC ROUTES — no token needed
    // ════════════════════════════════════════════════

    @GetMapping("/api/restaurants/public/all")
    @Operation(summary = "Get all restaurants")
    public ResponseEntity<List<RestaurantResponse>> getAll() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/api/restaurants/public/{id}")
    @Operation(summary = "Get restaurant by ID")
    public ResponseEntity<RestaurantResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @GetMapping("/api/restaurants/public/search")
    @Operation(summary = "Search restaurants by name or city")
    public ResponseEntity<List<RestaurantResponse>> search(
            @RequestParam String keyword) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(keyword));
    }

    @GetMapping("/api/restaurants/public/city")
    @Operation(summary = "Get open restaurants in a city")
    public ResponseEntity<List<RestaurantResponse>> getByCity(
            @RequestParam String city) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByCity(city));
    }

    // ════════════════════════════════════════════════
    // OWNER ROUTES — RESTAURANT_OWNER token required
    // ════════════════════════════════════════════════

    @PostMapping("/api/restaurant-owner/create")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Create a new restaurant")
    public ResponseEntity<RestaurantResponse> create(
            @Valid @RequestBody RestaurantRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {   // ← gets logged-in user
        return ResponseEntity.ok(
                restaurantService.createRestaurant(request, userDetails.getUsername()));
    }

    @PutMapping("/api/restaurant-owner/update")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Update your restaurant details")
    public ResponseEntity<RestaurantResponse> update(
            @Valid @RequestBody RestaurantRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                restaurantService.updateRestaurant(request, userDetails.getUsername()));
    }

    @PatchMapping("/api/restaurant-owner/toggle-status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Toggle restaurant open/closed status")
    public ResponseEntity<RestaurantResponse> toggleStatus(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                restaurantService.toggleOpenStatus(userDetails.getUsername()));
    }

    // ════════════════════════════════════════════════
    // ADMIN ROUTES — ADMIN token required
    // ════════════════════════════════════════════════

    @DeleteMapping("/api/admin/restaurants/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a restaurant (Admin only)")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok("Restaurant deleted successfully");
    }
}
