package com.infosys.foodapp.service;


import com.infosys.foodapp.dto.request.LoginRequest;
import com.infosys.foodapp.dto.request.RegisterRequest;
import com.infosys.foodapp.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}