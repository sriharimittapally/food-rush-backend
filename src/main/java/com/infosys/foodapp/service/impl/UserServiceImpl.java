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
    public UserResponse updateProfile(UpdateProfileRequest request,
                                      String email) {
        User user = getUserByEmail(email);

        // Check phone not taken by someone else
        if (!user.getPhone().equals(request.getPhone()) &&
                userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException(
                    "Phone number already in use by another account");
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

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