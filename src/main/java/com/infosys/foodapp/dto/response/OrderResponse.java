package com.infosys.foodapp.dto.response;

import com.infosys.foodapp.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private OrderStatus status;
    private Double totalAmount;
    private String deliveryAddress;
    private String specialInstructions;

    // Customer info
    private Long customerId;
    private String customerName;
    private String customerEmail;

    // Restaurant info
    private Long restaurantId;
    private String restaurantName;
    private String restaurantAddress;

    // Items
    private List<OrderItemResponse> orderItems;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}