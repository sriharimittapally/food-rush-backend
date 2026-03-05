package com.infosys.foodapp.dto.response;


import com.infosys.foodapp.enums.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String fullName;
    private Role role;
    private String message;
}
