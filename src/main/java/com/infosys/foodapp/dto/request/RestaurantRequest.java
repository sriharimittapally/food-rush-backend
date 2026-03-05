package com.infosys.foodapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String imageUrl;

    private String cuisineType;   // e.g. "Indian, Chinese"
}