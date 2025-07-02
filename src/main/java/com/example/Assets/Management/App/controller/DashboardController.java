package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.dto.responseDto.AssetResponseDTO;
import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.PurchaseHistory;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.PurchaseHistoryRepository;
import com.example.Assets.Management.App.service.AssetService;
import com.example.Assets.Management.App.service.UserService;
import com.example.Assets.Management.App.service.DashboardService;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryResponseDTO;
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
    
    @Autowired
    private DashboardService dashboardService;
    
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            return ResponseEntity.ok(dashboardService.getDashboardStats());
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error fetching dashboard statistics: " + e.getMessage());
            errorResponse.put("status", 400);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // New endpoints for DashboardService methods
    @GetMapping("/assets")
    public ResponseEntity<List<AssetResponseDTO>> getAllAssets() {
        return ResponseEntity.ok(dashboardService.getAllAssets());
    }

    @GetMapping("/assets/assigned")
    public ResponseEntity<List<AssetResponseDTO>> getAssignedAssets() {
        return ResponseEntity.ok(dashboardService.getAssignedAssets());
    }

    @GetMapping("/assets/non-assigned")
    public ResponseEntity<List<AssetResponseDTO>> getNonAssignedAssets() {
        return ResponseEntity.ok(dashboardService.getNonAssignedAssets());
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<List<PurchaseHistoryResponseDTO>> getExpiringSoon() {
        return ResponseEntity.ok(dashboardService.getExpiringSoon());
    }

    @GetMapping("/expired")
    public ResponseEntity<List<PurchaseHistoryResponseDTO>> getExpired() {
        return ResponseEntity.ok(dashboardService.getExpired());
    }

    @GetMapping("/total-assets")
    public ResponseEntity<Long> getTotalAssets() {
        return ResponseEntity.ok(dashboardService.getTotalAssets());
    }

    @GetMapping("/total-users")
    public ResponseEntity<Long> getTotalUsers() {
        return ResponseEntity.ok(dashboardService.getTotalUsers());
    }

    @GetMapping("/category-wise")
    public ResponseEntity<List<Map<String, Object>>> getCategoryWiseAssets() {
        return ResponseEntity.ok(dashboardService.getCategoryWiseAssets());
    }
}
