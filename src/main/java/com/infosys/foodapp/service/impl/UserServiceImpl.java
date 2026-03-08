package com.infosys.foodapp.service.impl;

import com.infosys.foodapp.dto.request.ChangePasswordRequest;
import com.infosys.foodapp.dto.request.UpdateProfileRequest;
import com.infosys.foodapp.dto.response.UserResponse;
import com.infosys.foodapp.entity.User;
import com.infosys.foodapp.repository.UserRepository;
import com.infosys.foodapp.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ─── Helper ───────────────────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .imageUrl(user.getImageUrl())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // ─── Get My Profile ───────────────────────────────────────

    @Override
    public UserResponse getMyProfile(String email) {
        return mapToResponse(getUserByEmail(email));
    }

    // ─── Update Profile ───────────────────────────────────────

    @Override
    public UserResponse updateProfile(UpdateProfileRequest req, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getFullName() != null && !req.getFullName().isBlank())
            user.setFullName(req.getFullName());

        if (req.getPhone() != null && !req.getPhone().isBlank())
            user.setPhone(req.getPhone());

        // ← make sure this is here
        if (req.getImageUrl() != null && !req.getImageUrl().isBlank())
            user.setImageUrl(req.getImageUrl());

        return mapToResponse(userRepository.save(user));
    }

    // ─── Change Password ──────────────────────────────────────

    @Override
    public void changePassword(ChangePasswordRequest request, String email) {
        User user = getUserByEmail(email);

        // 1. Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(),
                user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // 2. Check new and confirm match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException(
                    "New password and confirm password do not match");
        }

        // 3. Check new password is not same as old
        if (passwordEncoder.matches(request.getNewPassword(),
                user.getPassword())) {
            throw new RuntimeException(
                    "New password cannot be same as current password");
        }

        // 4. Save encoded new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}