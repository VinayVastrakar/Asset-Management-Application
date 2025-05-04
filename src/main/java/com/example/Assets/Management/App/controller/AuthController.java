package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.UserRepository;
import com.example.Assets.Management.App.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return Map.of("token", token, "user", user);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginData.get("email"), loginData.get("password"))
        );
        Users user = userRepository.findByEmail(loginData.get("email")).orElseThrow();
        String token = jwtUtil.generateToken(user.getEmail());
        return Map.of("token", token, "user", user);
    }
}
