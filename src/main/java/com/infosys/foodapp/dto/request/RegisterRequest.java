package com.infosys.foodapp.dto.request;


import com.infosys.foodapp.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Enter a valid 10-digit phone number")
    private String phone;

    @NotNull(message = "Role is required")
    private Role role;   // CUSTOMER, RESTAURANT_OWNER, ADMIN
}