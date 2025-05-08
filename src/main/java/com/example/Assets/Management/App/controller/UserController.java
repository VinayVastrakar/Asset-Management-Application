package com.example.Assets.Management.App.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Controller", description = "User APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    @Operation(summary = "Get All Users")
    public List<Map<String, Object>> getUserDetails() {
        List<Users> users = userRepository.findAll();
        List<Map<String, Object>> userList = new ArrayList<>();

        for (Users user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("name", user.getName());
            userMap.put("role", user.getRole());
            userList.add(userMap);
        }

        return userList;
    }
    
}
