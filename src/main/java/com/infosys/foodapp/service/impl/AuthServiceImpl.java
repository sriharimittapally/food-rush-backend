package com.infosys.foodapp.service.impl;


import com.infosys.foodapp.dto.request.LoginRequest;
import com.infosys.foodapp.dto.request.RegisterRequest;
import com.infosys.foodapp.dto.response.AuthResponse;
import com.infosys.foodapp.entity.User;
import com.infosys.foodapp.repository.UserRepository;
import com.infosys.foodapp.security.JwtUtil;
import com.infosys.foodapp.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ✅ Explicit constructor (no Lombok)
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    // ─── Register ─────────────────────────────────────────────

    @Override
    public AuthResponse register(RegisterRequest request) {

        // 1. Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                    "Email already registered: " + request.getEmail());
        }

        // 2. Check duplicate phone
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException(
                    "Phone number already registered: " + request.getPhone());
        }

        // 3. Build and save user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // ← BCrypt
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setActive(true);

        userRepository.save(user);

        // 4. Generate JWT token
        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .message("Registration successful!")
                .build();
    }

    // ─── Login ────────────────────────────────────────────────

    @Override
    public AuthResponse login(LoginRequest request) {

        // 1. Authenticate — throws BadCredentialsException if wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Load user from DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 3. Generate token
        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .message("Login successful!")
                .build();
    }
}
