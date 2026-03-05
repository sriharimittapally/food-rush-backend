package com.infosys.foodapp.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private String category;
    private boolean isVeg;
    private boolean isAvailable;
    private Long restaurantId;
    private String restaurantName;
}