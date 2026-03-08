package com.infosys.foodapp.service.impl;

import com.infosys.foodapp.dto.request.MenuItemRequest;
import com.infosys.foodapp.dto.response.MenuItemResponse;
import com.infosys.foodapp.entity.MenuItem;
import com.infosys.foodapp.entity.Restaurant;
import com.infosys.foodapp.entity.User;
import com.infosys.foodapp.repository.MenuItemRepository;
import com.infosys.foodapp.repository.RestaurantRepository;
import com.infosys.foodapp.repository.UserRepository;
import com.infosys.foodapp.service.MenuItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository,
                               RestaurantRepository restaurantRepository,
                               UserRepository userRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    // ─── Helpers ──────────────────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found with id: " + id));
    }

    private MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Menu item not found with id: " + id));
    }

    // ─── Security check — does this item belong to owner? ─────

    private void verifyOwnership(MenuItem item, String ownerEmail) {
        User owner = getUserByEmail(ownerEmail);
        Restaurant ownerRestaurant = restaurantRepository
                .findByOwner(owner)
                .orElseThrow(() ->
                        new RuntimeException("No restaurant found for this owner"));

        if (!item.getRestaurant().getId().equals(ownerRestaurant.getId())) {
            throw new RuntimeException(
                    "You are not authorized to modify this menu item");
        }
    }

    // ─── Entity → Response DTO ────────────────────────────────

    private MenuItemResponse mapToResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .category(item.getCategory())
                .isVeg(item.isVeg())
                .isAvailable(item.isAvailable())
                .restaurantId(item.getRestaurant().getId())
                .restaurantName(item.getRestaurant().getName())
                .build();
    }

    // ─── Add Menu Item ────────────────────────────────────────

    @Override
    public MenuItemResponse addMenuItem(MenuItemRequest request,
                                        Long restaurantId,
                                        String ownerEmail) {
        User owner = getUserByEmail(ownerEmail);
        Restaurant restaurant = getRestaurantById(restaurantId);

        // Verify this restaurant belongs to the owner making the request
        if (!restaurant.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException(
                    "You are not authorized to add items to this restaurant");
        }

        MenuItem item = new MenuItem();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setCategory(request.getCategory());
        item.setVeg(request.isVeg());
        item.setAvailable(true);
        item.setRestaurant(restaurant);

        return mapToResponse(menuItemRepository.save(item));
    }

    // ─── Update Menu Item ─────────────────────────────────────

    @Override
    public MenuItemResponse updateMenuItem(Long itemId,
                                           MenuItemRequest request,
                                           String ownerEmail) {
        MenuItem item = getMenuItemById(itemId);
        verifyOwnership(item, ownerEmail);   // security check

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setCategory(request.getCategory());
        item.setVeg(request.isVeg());

        return mapToResponse(menuItemRepository.save(item));
    }

    // ─── Delete Menu Item ─────────────────────────────────────

    @Override
    public void deleteMenuItem(Long itemId, String ownerEmail) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        if (!item.getRestaurant().getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        item.setDeleted(true);
        item.setAvailable(false);
        menuItemRepository.save(item);
    }

    // ─── Toggle Availability ──────────────────────────────────

    @Override
    public MenuItemResponse toggleAvailability(Long itemId, String ownerEmail) {
        MenuItem item = getMenuItemById(itemId);
        verifyOwnership(item, ownerEmail);   // security check

        item.setAvailable(!item.isAvailable());   // flip status
        return mapToResponse(menuItemRepository.save(item));
    }

    // ─── Get Full Menu (Public) ───────────────────────────────

    @Override
    public List<MenuItemResponse> getMenuByRestaurant(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        return menuItemRepository.findByRestaurantAndDeletedFalse(restaurant)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> getAvailableMenu(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        return menuItemRepository
                .findByRestaurantAndIsAvailableTrueAndDeletedFalse(restaurant)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Filter By Category (Public) ─────────────────────────

    @Override
    public List<MenuItemResponse> getMenuByCategory(Long restaurantId,
                                                    String category) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        return menuItemRepository
                .findByRestaurantAndCategory(restaurant, category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Veg Only Filter (Public) ─────────────────────────────

    @Override
    public List<MenuItemResponse> getVegItems(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        return menuItemRepository.findByRestaurantAndIsVegTrue(restaurant)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}