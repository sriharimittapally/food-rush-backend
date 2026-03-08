package com.infosys.foodapp.controller;

import com.infosys.foodapp.dto.response.*;
import com.infosys.foodapp.service.AdminService;
import com.infosys.foodapp.service.OrderService;
import com.infosys.foodapp.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final AdminService adminService;

    private final RestaurantService restaurantService;
    private final OrderService orderService;

    public AdminController(AdminService adminService,
                           RestaurantService restaurantService,
                           OrderService orderService) {
        this.adminService       = adminService;
        this.restaurantService  = restaurantService;
        this.orderService       = orderService;
    }
    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard stats")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/customers")
    @Operation(summary = "Get all customers")
    public ResponseEntity<List<UserResponse>> getAllCustomers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    @PatchMapping("/users/{userId}/block-unblock")
    @Operation(summary = "Block or unblock a user")
    public ResponseEntity<UserResponse> blockUnblock(
            @PathVariable Long userId) {
        return ResponseEntity.ok(adminService.blockUnblockUser(userId));
    }

    @GetMapping("/restaurants")
    @Operation(summary = "Get all restaurants")
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders (Admin)")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    @GetMapping("/revenue-report")
    @Operation(summary = "Get revenue report for all restaurants")
    public ResponseEntity<List<RestaurantRevenueResponse>> getRevenueReport() {
        return ResponseEntity.ok(adminService.getRevenueReport());
    }
}
