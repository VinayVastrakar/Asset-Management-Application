package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.Enums.Role;
import com.example.Assets.Management.App.Enums.Status;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.UserRepository;
import com.example.Assets.Management.App.security.JwtUtil;
import com.example.Assets.Management.App.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Controller", description = "User APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public UserController(EmailService emailService, PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @Operation(summary = "Get All Users (Paginated)")
    public Map<String, Object> getUserDetails(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> usersPage = userRepository.findAll(pageable);
        System.out.println("------------>"+usersPage);
        List<Map<String, Object>> userList = new ArrayList<>();
        for (Users user : usersPage.getContent()) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("name", user.getName());
            userMap.put("role", user.getRole());
            userMap.put("status",user.getStatus());
            userList.add(userMap);
        }

        return Map.of(
            "users", userList,
            "currentPage", usersPage.getNumber(),
            "totalItems", usersPage.getTotalElements(),
            "totalPages", usersPage.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User By ID")
    public Map<String, Object> getUserById(@PathVariable Long id){
        Users user = userRepository.findById(id).get();
        return Map.of(
            "user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole(),
                "mobileNo",user.getMobileNumber(),
                "status",user.getStatus()
            )
        );

    }

    @PostMapping("/register")
    @Operation(summary = "User Registration")
    public Map<String, Object> register(@RequestBody Users user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return Map.of("error", "Email cannot be null or blank.");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return Map.of("error", "Password cannot be null or blank.");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return Map.of("error", "User with this email already exists.");
        }

        user.setStatus(user.getStatus() != null ? user.getStatus() : Status.Active);
        user.setRole(user.getRole() != null ? user.getRole() : Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getName(), user.getRole());
            // smsService.sendWelcomeSms(user.getMobileNumber(), user.getName(), user.getRole());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Some Issue on mail service");
        }

        return Map.of(
            "user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole()
            )
        );
    }

        @PutMapping("/inactive/{id}")
        @Operation(summary = "Deactivate User")
        public ResponseEntity<?> inactiveUser(@PathVariable long id) {
            Optional<Users> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Users user = userOpt.get();
            user.setStatus(Status.Inactive);
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "message", "User deactivated successfully",
                "user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "status", user.getStatus()
                )
            ));
        }

        @PutMapping("/active/{id}")
        @Operation(summary = "Activate User")
        public ResponseEntity<?> activeUser(@PathVariable long id) {
            Optional<Users> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Users user = userOpt.get();
            user.setStatus(Status.Active);
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "message", "User activated successfully",
                "user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "status", user.getStatus()
                )
            ));
        }
}
