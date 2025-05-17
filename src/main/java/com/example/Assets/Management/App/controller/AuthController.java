package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.Enums.Role;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.UserRepository;
import com.example.Assets.Management.App.security.JwtUtil;
import com.example.Assets.Management.App.service.EmailService;
import com.example.Assets.Management.App.service.OtpService;
import com.example.Assets.Management.App.service.SmsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth APIs")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "User Registeration")
    public Map<String, Object> register(@RequestBody Users user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return Map.of("error", "User with this email already exists.");
        }
    
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole(Role.USER);
        userRepository.save(user);
    
        // Send welcome email and SMS
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getName(), user.getRole());
        // smsService.sendWelcomeSms(user.getMobileNumber(), user.getName(), user.getRole()); // Ensure phone field exists in Users
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("Some Issue on mail service");
        }
        
    
        String token = jwtUtil.generateToken(user.getEmail());
    
        return Map.of(
            "token", token,
            "user", Map.of("id", user.getId(), "email", user.getEmail(), "name", user.getName(), "role", user.getRole())
        );
    }

    @PostMapping("/login")
@Operation(summary = "User Login")
public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
    System.err.println("loginData: " + loginData);
    
    // Authenticate first - this will throw AuthenticationException if credentials are wrong
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginData.get("email"), 
            loginData.get("password")
        )
    );

    // If authentication succeeds, proceed
    Users user = userRepository.findByEmail(loginData.get("email"))
        .orElseThrow(() -> new RuntimeException("User not found"));

    String token = jwtUtil.generateToken(user.getEmail());
    String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

    return Map.of(
        "token", token,
        "refreshToken", refreshToken,
        "user", Map.of(
            "id", user.getId(), 
            "email", user.getEmail(), 
            "name", user.getName(), 
            "role", user.getRole()
        )
    );
}

    @GetMapping("/user")
    @Operation(summary = "Get User With AuthKey")
    public Map<String, Object> getUserDetails(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Now fetch your own Users entity from the database
        Users users = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found")); // Make sure this method exists

        return Map.of(
            "id", users.getId(),
            "email", users.getEmail(),
            "name", users.getName(),
            "role", users.getRole()
        );
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forget Password")
    public Map<String, String> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        if (userRepository.findByEmail(email).isEmpty()) {
            return Map.of("error", "User not found");
        }

        String otp = otpService.generateOtp(email);
        // sendOtpToEmailOrSms(email, otp);
        System.out.println(otp);
        return Map.of("message", "OTP sent to your email");
    }

    @PostMapping("/validate-otp")
    @Operation(summary = "Validate OTP")
    public Map<String, Object> validateOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");

        if (userRepository.findByEmail(email).isEmpty()) {
            return Map.of("error", "User not found");
        }

        boolean isValid = otpService.validateOtp(email, otp);
        
        if (isValid) {
            return Map.of(
                "isValid", true,
                "message", "OTP validated successfully"
            );
        } else {
            return Map.of(
                "isValid", false,
                "error", "Invalid or expired OTP"
            );
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset User Password")
    public Map<String, String> resetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");
        String newPassword = payload.get("newPassword");

        // if (!otpService.validateOtp(email, otp)) {
        //     return Map.of("error", "Invalid or expired OTP");
        // }

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return Map.of("message", "Password has been reset successfully");
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh JWT Token")
    public Map<String, String> refreshToken(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");
        
        if (refreshToken == null || !jwtUtil.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        String email = jwtUtil.getUsernameFromToken(refreshToken);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String newToken = jwtUtil.generateToken(email);
        
        return Map.of(
            "token", newToken,
            "message", "Token refreshed successfully"
        );
    }
}
