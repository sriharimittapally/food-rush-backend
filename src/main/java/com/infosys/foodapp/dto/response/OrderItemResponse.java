package com.infosys.foodapp.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private String category;
    private boolean isVeg;
    private Integer quantity;
    private Double price;          // price at time of order
    private Double subtotal;       // price × quantity
}