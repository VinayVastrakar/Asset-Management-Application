package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.dto.responseDto.AssetResponseDTO;
import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.PurchaseHistory;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.PurchaseHistoryRepository;
import com.example.Assets.Management.App.service.AssetService;
import com.example.Assets.Management.App.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    @Autowired
    private AssetService assetService;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            // Get total assets count
            List<AssetResponseDTO> assets = assetService.getAllAssets();
            long totalAssets = assets.size();
            
            // Get total users count
            List<Users> users = userService.getAllUsers();
            long totalUsers = users.size();
            
            // Get assets by category
            Map<String, Long> categoryCount = assets.stream()
                .collect(Collectors.groupingBy(
                    asset -> asset.getCategoryName(),
                    Collectors.counting()
                ));
            
            // Convert to required format
            List<Map<String, Object>> categoryWise = categoryCount.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> category = new HashMap<>();
                    category.put("category", entry.getKey());
                    category.put("count", entry.getValue());
                    return category;
                })
                .collect(Collectors.toList());

            // Calculate expiring soon (within next 30 days) from PurchaseHistory
            LocalDate now = LocalDate.now();
            LocalDate oneMonthFromNow = now.plusMonths(1);
            long expiringSoonCount = purchaseHistoryRepository.findByExpiryDateBetween(now, oneMonthFromNow)
                .stream()
                .filter(history -> "Yes".equalsIgnoreCase(history.getNotify()))
                .count();

            // Calculate expired assets from PurchaseHistory
            long expiredAssetCount = purchaseHistoryRepository.findByExpiryDateBefore(now)
                .stream()
                .filter(history -> "Yes".equalsIgnoreCase(history.getNotify()))
                .count();
            
            // Assigned / Non-Assigned count
            long assignedAssetCount = assets.stream()
                .filter(asset -> asset.getAssignedToUserName() != null)
                .count();

            long nonAssignedAssetCount = totalAssets - assignedAssetCount;

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("data", Map.of(
                "totalAssets", totalAssets,
                "totalUsers", totalUsers,
                "categoryWise", categoryWise,
                "expiringSoonCount", expiringSoonCount,
                "expiredAssets", expiredAssetCount,
                "assignedAssets", assignedAssetCount,
                "nonAssignedAssets", nonAssignedAssetCount
            ));
            response.put("message", "Dashboard statistics retrieved successfully");
            response.put("status", 200);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error fetching dashboard statistics: " + e.getMessage());
            errorResponse.put("status", 400);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
