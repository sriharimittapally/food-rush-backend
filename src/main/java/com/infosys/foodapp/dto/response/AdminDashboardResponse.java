package com.infosys.foodapp.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    // User stats
    private Long totalUsers;
    private Long totalCustomers;
    private Long totalRestaurantOwners;

    // Restaurant stats
    private Long totalRestaurants;
    private Long openRestaurants;

    // Order stats
    private Long totalOrders;
    private Long pendingOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
}