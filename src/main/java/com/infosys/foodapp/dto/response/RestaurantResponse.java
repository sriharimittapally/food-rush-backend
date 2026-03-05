package com.infosys.foodapp.dto.response;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String phone;
    private String imageUrl;
    private String cuisineType;
    private Double rating;
    private boolean isOpen;
    private String ownerName;
    private String ownerEmail;
    private LocalDateTime createdAt;
}