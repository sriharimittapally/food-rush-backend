package com.infosys.foodapp.service;

import com.infosys.foodapp.dto.request.OrderRequest;
import com.infosys.foodapp.dto.request.UpdateOrderStatusRequest;
import com.infosys.foodapp.dto.response.OrderResponse;
import com.infosys.foodapp.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    // Customer
    OrderResponse placeOrder(OrderRequest request, String customerEmail);
    List<OrderResponse> getMyOrders(String customerEmail);
    OrderResponse getOrderById(Long orderId, String userEmail);
    OrderResponse cancelOrder(Long orderId, String customerEmail);

    // Restaurant Owner
    List<OrderResponse> getRestaurantOrders(String ownerEmail);
    List<OrderResponse> getRestaurantOrdersByStatus(String ownerEmail,
                                                    OrderStatus status);
    OrderResponse updateOrderStatus(Long orderId,
                                    UpdateOrderStatusRequest request,
                                    String ownerEmail);

    // Admin
    List<OrderResponse> getAllOrders();
}