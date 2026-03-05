package com.infosys.foodapp.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class LoginRequest {

    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
