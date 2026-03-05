package com.infosys.foodapp.controller;

import com.infosys.foodapp.dto.request.MenuItemRequest;
import com.infosys.foodapp.dto.response.MenuItemResponse;
import com.infosys.foodapp.service.MenuItemService;
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
@Tag(name = "Menu Items", description = "Menu management APIs")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    // ════════════════════════════════════════════════
    // PUBLIC ROUTES — no token needed
    // ════════════════════════════════════════════════

    @GetMapping("/api/restaurants/public/{restaurantId}/menu")
    @Operation(summary = "Get full menu of a restaurant")
    public ResponseEntity<List<MenuItemResponse>> getMenu(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(
                menuItemService.getMenuByRestaurant(restaurantId));
    }

    @GetMapping("/api/restaurants/public/{restaurantId}/menu/available")
    @Operation(summary = "Get only available items")
    public ResponseEntity<List<MenuItemResponse>> getAvailableMenu(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(
                menuItemService.getAvailableMenu(restaurantId));
    }

    @GetMapping("/api/restaurants/public/{restaurantId}/menu/category")
    @Operation(summary = "Filter menu by category")
    public ResponseEntity<List<MenuItemResponse>> getByCategory(
            @PathVariable Long restaurantId,
            @RequestParam String category) {
        return ResponseEntity.ok(
                menuItemService.getMenuByCategory(restaurantId, category));
    }

    @GetMapping("/api/restaurants/public/{restaurantId}/menu/veg")
    @Operation(summary = "Get veg only items")
    public ResponseEntity<List<MenuItemResponse>> getVegItems(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(
                menuItemService.getVegItems(restaurantId));
    }

    // ════════════════════════════════════════════════
    // OWNER ROUTES — RESTAURANT_OWNER token required
    // ════════════════════════════════════════════════

    @PostMapping("/api/restaurant-owner/{restaurantId}/menu/add")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Add a new menu item")
    public ResponseEntity<MenuItemResponse> addItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                menuItemService.addMenuItem(
                        request, restaurantId, userDetails.getUsername()));
    }

    @PutMapping("/api/restaurant-owner/menu/{itemId}/update")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Update a menu item")
    public ResponseEntity<MenuItemResponse> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody MenuItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                menuItemService.updateMenuItem(
                        itemId, request, userDetails.getUsername()));
    }

    @DeleteMapping("/api/restaurant-owner/menu/{itemId}/delete")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Delete a menu item")
    public ResponseEntity<String> deleteItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        menuItemService.deleteMenuItem(itemId, userDetails.getUsername());
        return ResponseEntity.ok("Menu item deleted successfully");
    }

    @PatchMapping("/api/restaurant-owner/menu/{itemId}/toggle-availability")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Toggle item availability")
    public ResponseEntity<MenuItemResponse> toggleAvailability(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                menuItemService.toggleAvailability(
                        itemId, userDetails.getUsername()));
    }
}