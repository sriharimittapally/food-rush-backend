package com.infosys.foodapp.controller;

import com.infosys.foodapp.dto.request.OrderRequest;
import com.infosys.foodapp.dto.request.UpdateOrderStatusRequest;
import com.infosys.foodapp.dto.response.OrderResponse;
import com.infosys.foodapp.enums.OrderStatus;
import com.infosys.foodapp.service.OrderService;
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
@RequestMapping("/api")
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ════════════════════════════════════════════════
    // CUSTOMER ROUTES
    // ════════════════════════════════════════════════

    @PostMapping("/orders/place")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Place a new order")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                orderService.placeOrder(request, userDetails.getUsername()));
    }

    @GetMapping("/orders/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get all my orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                orderService.getMyOrders(userDetails.getUsername()));
    }

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','RESTAURANT_OWNER','ADMIN')")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                orderService.getOrderById(orderId, userDetails.getUsername()));
    }

    @PatchMapping("/orders/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                orderService.cancelOrder(orderId, userDetails.getUsername()));
    }

    // ════════════════════════════════════════════════
    // RESTAURANT OWNER ROUTES
    // ════════════════════════════════════════════════

    @GetMapping("/restaurant-owner/orders")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Get all orders for my restaurant")
    public ResponseEntity<List<OrderResponse>> getRestaurantOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                orderService.getRestaurantOrders(userDetails.getUsername()));
    }

    @GetMapping("/restaurant-owner/orders/status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Get orders filtered by status")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @RequestParam OrderStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                orderService.getRestaurantOrdersByStatus(
                        userDetails.getUsername(), status));
    }

    @PatchMapping("/restaurant-owner/orders/{orderId}/status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        orderId, request, userDetails.getUsername()));
    }

    // ════════════════════════════════════════════════
    // ADMIN ROUTES
    // ════════════════════════════════════════════════




}
