// Location: src/main/java/com/todoapp/service/AuthService.java

package com.todoapp.service;

import com.todoapp.dto.AuthResponse;
import com.todoapp.dto.LoginRequest;
import com.todoapp.dto.RegisterRequest;
import com.todoapp.model.User;
import com.todoapp.repository.UserRepository;
import com.todoapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        try {
            log.info("Registration attempt for username: {}", request.getUsername());

            if (userRepository.existsByUsername(request.getUsername().trim())) {
                log.warn("Username already exists: {}", request.getUsername());
                return new AuthResponse(false, "Username already exists");
            }

            User user = new User();
            user.setUsername(request.getUsername().trim());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);

            log.info("User registered successfully: {}", request.getUsername());
            return new AuthResponse(true, "Registration successful!");

        } catch (Exception e) {
            log.error("Registration error: ", e);
            return new AuthResponse(false, "Registration failed");
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            log.info("Login attempt for username: {}", request.getUsername());

            User user = userRepository.findByUsername(request.getUsername().trim())
                    .orElse(null);

            if (user == null) {
                log.warn("User not found: {}", request.getUsername());
                return new AuthResponse(false, "Invalid credentials");
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Password mismatch for user: {}", request.getUsername());
                return new AuthResponse(false, "Invalid credentials");
            }

            String token = jwtUtil.generateToken(user.getId(), user.getUsername());

            log.info("Login successful for user: {}", request.getUsername());
            AuthResponse response = new AuthResponse();
            response.setSuccess(true);
            response.setToken(token);
            response.setUsername(user.getUsername());

            return response;

        } catch (Exception e) {
            log.error("Login error: ", e);
            return new AuthResponse(false, "Login failed");
        }
    }
}