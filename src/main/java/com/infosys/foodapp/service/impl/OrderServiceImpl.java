package com.infosys.foodapp.service.impl;

import com.infosys.foodapp.dto.request.OrderItemRequest;
import com.infosys.foodapp.dto.request.OrderRequest;
import com.infosys.foodapp.dto.request.UpdateOrderStatusRequest;
import com.infosys.foodapp.dto.response.OrderItemResponse;
import com.infosys.foodapp.dto.response.OrderResponse;
import com.infosys.foodapp.entity.*;
import com.infosys.foodapp.enums.OrderStatus;
import com.infosys.foodapp.repository.*;
import com.infosys.foodapp.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            UserRepository userRepository,
                            RestaurantRepository restaurantRepository,
                            MenuItemRepository menuItemRepository, SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.messagingTemplate = messagingTemplate;
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

    private Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found with id: " + id));
    }

    // ─── Entity → Response DTO ────────────────────────────────

    private OrderItemResponse mapItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItem().getId())
                .menuItemName(item.getMenuItem().getName())
                .category(item.getMenuItem().getCategory())
                .isVeg(item.getMenuItem().isVeg())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getPrice() * item.getQuantity())
                .build();
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems()
                .stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .specialInstructions(order.getSpecialInstructions())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getFullName())
                .customerEmail(order.getCustomer().getEmail())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .restaurantAddress(order.getRestaurant().getAddress())
                .orderItems(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    // ─── Place Order ──────────────────────────────────────────

    @Override
    @Transactional    // ← all or nothing — if anything fails, full rollback
    public OrderResponse placeOrder(OrderRequest request, String customerEmail) {

        // 1. Get customer
        User customer = getUserByEmail(customerEmail);

        // 2. Get restaurant
        Restaurant restaurant = getRestaurantById(request.getRestaurantId());

        // 3. Check restaurant is open
        if (!restaurant.isOpen()) {
            throw new RuntimeException(
                    "Restaurant is currently closed. Cannot place order.");
        }

        // 4. Build order items and calculate total
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (OrderItemRequest itemRequest : request.getItems()) {

            // Get menu item
            MenuItem menuItem = menuItemRepository
                    .findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException(
                            "Menu item not found: " + itemRequest.getMenuItemId()));

            // Verify item belongs to this restaurant
            if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                throw new RuntimeException(
                        "Menu item " + menuItem.getName() +
                                " does not belong to this restaurant");
            }

            // Verify item is available
            if (!menuItem.isAvailable()) {
                throw new RuntimeException(
                        menuItem.getName() + " is currently unavailable");
            }

            // Build order item with SNAPSHOT price
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(menuItem.getPrice());   // ← snapshot price

            totalAmount += menuItem.getPrice() * itemRequest.getQuantity();
            orderItems.add(orderItem);
        }

        // 5. Create and save order
        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setTotalAmount(Math.round(totalAmount * 100.0) / 100.0);
        // status = PENDING set automatically in @PrePersist

        Order savedOrder = orderRepository.save(order);

        // 6. Link order items to saved order and save them
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        return mapToResponse(savedOrder);
    }

    // ─── Get My Orders (Customer) ─────────────────────────────

    @Override
    public List<OrderResponse> getMyOrders(String customerEmail) {
        User customer = getUserByEmail(customerEmail);
        return orderRepository.findByCustomerOrderByCreatedAtDesc(customer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Get Order By ID ──────────────────────────────────────

    @Override
    public OrderResponse getOrderById(Long orderId, String userEmail) {
        Order order = getOrderById(orderId);
        User user = getUserByEmail(userEmail);

        // Security — customer can only see  own orders
        // Restaurant owner can see their restaurant's orders
        boolean isCustomer = order.getCustomer().getId().equals(user.getId());
        boolean isOwner = order.getRestaurant()
                .getOwner().getId().equals(user.getId());

        if (!isCustomer && !isOwner) {
            throw new RuntimeException(
                    "You are not authorized to view this order");
        }

        return mapToResponse(order);
    }

    // ─── Cancel Order (Customer) ──────────────────────────────

    @Override
    public OrderResponse cancelOrder(Long orderId, String customerEmail) {
        Order order = getOrderById(orderId);
        User customer = getUserByEmail(customerEmail);

        // Verify this order belongs to this customer
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException(
                    "You are not authorized to cancel this order");
        }

        // Can only cancel if PENDING or CONFIRMED
        if (order.getStatus() == OrderStatus.PREPARING ||
                order.getStatus() == OrderStatus.OUT_FOR_DELIVERY ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException(
                    "Order cannot be cancelled at this stage: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return mapToResponse(orderRepository.save(order));
    }

    // ─── Get Restaurant Orders (Owner) ───────────────────────

    @Override
    public List<OrderResponse> getRestaurantOrders(String ownerEmail) {
        User owner = getUserByEmail(ownerEmail);
        Restaurant restaurant = restaurantRepository
                .findByOwner(owner)
                .orElseThrow(() ->
                        new RuntimeException("No restaurant found for this owner"));

        return orderRepository
                .findByRestaurantOrderByCreatedAtDesc(restaurant)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Get Restaurant Orders By Status (Owner) ──────────────

    @Override
    public List<OrderResponse> getRestaurantOrdersByStatus(String ownerEmail,
                                                           OrderStatus status) {
        User owner = getUserByEmail(ownerEmail);
        Restaurant restaurant = restaurantRepository
                .findByOwner(owner)
                .orElseThrow(() ->
                        new RuntimeException("No restaurant found for this owner"));

        return orderRepository.findByRestaurantAndStatus(restaurant, status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Update Order Status (Owner) ──────────────────────────

    @Override
    public OrderResponse updateOrderStatus(Long orderId,
                                           UpdateOrderStatusRequest request,
                                           String ownerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(request.getStatus());
        Order saved = orderRepository.save(order);
        OrderResponse response = mapToResponse(saved);

        // ← ADD THIS — broadcast to all subscribers of this order
        messagingTemplate.convertAndSend(
                "/topic/orders/" + orderId,
                response
        );

        return response;
    }

    // ─── Status Transition Rules ──────────────────────────────

    private void validateStatusTransition(OrderStatus current,
                                          OrderStatus next) {
        // PENDING → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED
        switch (current) {
            case PENDING ->  {
                if (next != OrderStatus.CONFIRMED &&
                        next != OrderStatus.CANCELLED)
                    throw new RuntimeException(
                            "PENDING order can only move to CONFIRMED or CANCELLED");
            }
            case CONFIRMED -> {
                if (next != OrderStatus.PREPARING &&
                        next != OrderStatus.CANCELLED)
                    throw new RuntimeException(
                            "CONFIRMED order can only move to PREPARING or CANCELLED");
            }
            case PREPARING -> {
                if (next != OrderStatus.OUT_FOR_DELIVERY)
                    throw new RuntimeException(
                            "PREPARING order can only move to OUT_FOR_DELIVERY");
            }
            case OUT_FOR_DELIVERY -> {
                if (next != OrderStatus.DELIVERED)
                    throw new RuntimeException(
                            "OUT_FOR_DELIVERY order can only move to DELIVERED");
            }
            case DELIVERED, CANCELLED -> throw new RuntimeException(
                    "Cannot change status of a " + current + " order");
        }
    }

    // ─── Get All Orders (Admin) ───────────────────────────────

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}