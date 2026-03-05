package com.infosys.foodapp.service;

import com.infosys.foodapp.dto.request.ChangePasswordRequest;
import com.infosys.foodapp.dto.request.UpdateProfileRequest;
import com.infosys.foodapp.dto.response.UserResponse;

public interface UserService {
    UserResponse getMyProfile(String email);
    UserResponse updateProfile(UpdateProfileRequest request, String email);
    void changePassword(ChangePasswordRequest request, String email);
}