package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.UserRepository;
import com.example.Assets.Management.App.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Users user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return Map.of("error", "User with this email already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("ROLE_USER");
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());

        return Map.of(
            "token", token,
            "user", Map.of("id", user.getId(), "email", user.getEmail(), "name", user.getName(), "role", user.getRole())
        );
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginData.get("email"), loginData.get("password")
                )
            );
        } catch (AuthenticationException e) {
            return Map.of("error", "Invalid email or password");
        }

        Users user = userRepository.findByEmail(loginData.get("email"))
            .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());

        return Map.of(
            "token", token,
            "user", Map.of("id", user.getId(), "email", user.getEmail(), "name", user.getName(), "role", user.getRole())
        );
    }
}
