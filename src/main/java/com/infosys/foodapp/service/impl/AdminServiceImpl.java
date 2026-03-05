package com.infosys.foodapp.service.impl;

import com.infosys.foodapp.dto.response.AdminDashboardResponse;
import com.infosys.foodapp.dto.response.RestaurantRevenueResponse;
import com.infosys.foodapp.dto.response.UserResponse;
import com.infosys.foodapp.entity.Restaurant;
import com.infosys.foodapp.entity.User;
import com.infosys.foodapp.enums.OrderStatus;
import com.infosys.foodapp.enums.Role;
import com.infosys.foodapp.repository.OrderRepository;
import com.infosys.foodapp.repository.RestaurantRepository;
import com.infosys.foodapp.repository.UserRepository;
import com.infosys.foodapp.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            RestaurantRepository restaurantRepository,
                            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
    }

    // ─── Helper ───────────────────────────────────────────────

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // ─── Dashboard ────────────────────────────────────────────

    @Override
    public AdminDashboardResponse getDashboard() {

        // User counts
        long totalUsers       = userRepository.count();
        long totalCustomers   = userRepository
                .findByRole(Role.CUSTOMER).size();
        long totalOwners      = userRepository
                .findByRole(Role.RESTAURANT_OWNER).size();

        // Restaurant counts
        long totalRestaurants = restaurantRepository.count();
        long openRestaurants  = restaurantRepository
                .findAll().stream()
                .filter(Restaurant::isOpen)
                .count();

        // Order counts
        long totalOrders     = orderRepository.count();
        long pendingOrders   = orderRepository
                .countByStatus(OrderStatus.PENDING);
        long deliveredOrders = orderRepository
                .countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository
                .countByStatus(OrderStatus.CANCELLED);

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalCustomers(totalCustomers)
                .totalRestaurantOwners(totalOwners)
                .totalRestaurants(totalRestaurants)
                .openRestaurants(openRestaurants)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .build();
    }

    // ─── Get All Users ────────────────────────────────────────

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // ─── Get All Customers ────────────────────────────────────

    @Override
    public List<UserResponse> getAllCustomers() {
        return userRepository.findByRole(Role.CUSTOMER)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // ─── Block / Unblock User ─────────────────────────────────

    @Override
    public UserResponse blockUnblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + userId));

        // Prevent admin from blocking themselves
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot block an Admin account");
        }

        // Flip active status
        user.setActive(!user.isActive());
        return mapToUserResponse(userRepository.save(user));
    }

    // ─── Revenue Report ───────────────────────────────────────

    @Override
    public List<RestaurantRevenueResponse> getRevenueReport() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurant -> {

                    // Get total revenue from delivered orders
                    Double revenue = orderRepository
                            .getTotalRevenueByRestaurant(restaurant);

                    // Get total delivered order count
                    Long deliveredCount = (long) orderRepository
                            .findByRestaurantAndStatus(
                                    restaurant, OrderStatus.DELIVERED)
                            .size();

                    return RestaurantRevenueResponse.builder()
                            .restaurantId(restaurant.getId())
                            .restaurantName(restaurant.getName())
                            .city(restaurant.getCity())
                            .ownerName(restaurant.getOwner().getFullName())
                            .totalRevenue(revenue != null ? revenue : 0.0)
                            .totalDeliveredOrders(deliveredCount)
                            .build();
                })
                .collect(Collectors.toList());
    }
}