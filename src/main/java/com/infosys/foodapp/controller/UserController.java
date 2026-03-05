package com.infosys.foodapp.controller;

import com.infosys.foodapp.dto.request.ChangePasswordRequest;
import com.infosys.foodapp.dto.request.UpdateProfileRequest;
import com.infosys.foodapp.dto.response.UserResponse;
import com.infosys.foodapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Profile", description = "User profile management APIs")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('CUSTOMER','RESTAURANT_OWNER','ADMIN')")
    @Operation(summary = "Get my profile")
    public ResponseEntity<UserResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                userService.getMyProfile(userDetails.getUsername()));
    }

    @PutMapping("/profile/update")
    @PreAuthorize("hasAnyRole('CUSTOMER','RESTAURANT_OWNER','ADMIN')")
    @Operation(summary = "Update my profile")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                userService.updateProfile(request, userDetails.getUsername()));
    }

    @PatchMapping("/change-password")
    @PreAuthorize("hasAnyRole('CUSTOMER','RESTAURANT_OWNER','ADMIN')")
    @Operation(summary = "Change my password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.changePassword(request, userDetails.getUsername());
        return ResponseEntity.ok("Password changed successfully");
    }
}