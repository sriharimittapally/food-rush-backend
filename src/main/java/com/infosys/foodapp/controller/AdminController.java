package com.infosys.foodapp.controller;

import com.infosys.foodapp.dto.response.AdminDashboardResponse;
import com.infosys.foodapp.dto.response.RestaurantRevenueResponse;
import com.infosys.foodapp.dto.response.UserResponse;
import com.infosys.foodapp.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")       // ← applies to ALL methods in this controller
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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

    @GetMapping("/revenue-report")
    @Operation(summary = "Get revenue report for all restaurants")
    public ResponseEntity<List<RestaurantRevenueResponse>> getRevenueReport() {
        return ResponseEntity.ok(adminService.getRevenueReport());
    }
}
