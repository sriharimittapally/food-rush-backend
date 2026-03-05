package com.infosys.foodapp.service;

import com.infosys.foodapp.dto.request.MenuItemRequest;
import com.infosys.foodapp.dto.response.MenuItemResponse;

import java.util.List;

public interface MenuItemService {

    // Owner
    MenuItemResponse addMenuItem(MenuItemRequest request,
                                 Long restaurantId, String ownerEmail);
    MenuItemResponse updateMenuItem(Long itemId, MenuItemRequest request,
                                    String ownerEmail);
    void deleteMenuItem(Long itemId, String ownerEmail);
    MenuItemResponse toggleAvailability(Long itemId, String ownerEmail);

    // Public
    List<MenuItemResponse> getMenuByRestaurant(Long restaurantId);
    List<MenuItemResponse> getAvailableMenu(Long restaurantId);
    List<MenuItemResponse> getMenuByCategory(Long restaurantId, String category);
    List<MenuItemResponse> getVegItems(Long restaurantId);
}