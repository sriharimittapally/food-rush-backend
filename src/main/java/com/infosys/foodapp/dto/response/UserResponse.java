package com.infosys.foodapp.dto.response;

import com.infosys.foodapp.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private boolean isActive;
    private LocalDateTime createdAt;
}