package com.infosys.foodapp.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRevenueResponse {

    private Long restaurantId;
    private String restaurantName;
    private String city;
    private String ownerName;
    private Double totalRevenue;
    private Long totalDeliveredOrders;
}