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
import io.swagger.v3.oas.annotations.Operation;

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
    
    @Operation(summary = "Get dashboard statistics")
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
    @Operation(summary = "Get all assets for dashboard")
    @GetMapping("/assets")
    public ResponseEntity<List<AssetResponseDTO>> getAllAssets() {
        return ResponseEntity.ok(dashboardService.getAllAssets());
    }

    @Operation(summary = "Get all assigned assets for dashboard")
    @GetMapping("/assets/assigned")
    public ResponseEntity<List<AssetResponseDTO>> getAssignedAssets() {
        return ResponseEntity.ok(dashboardService.getAssignedAssets());
    }

    @Operation(summary = "Get all non-assigned assets for dashboard")
    @GetMapping("/assets/non-assigned")
    public ResponseEntity<List<AssetResponseDTO>> getNonAssignedAssets() {
        return ResponseEntity.ok(dashboardService.getNonAssignedAssets());
    }

    @Operation(summary = "Get expiring soon purchase histories for dashboard")
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<PurchaseHistoryResponseDTO>> getExpiringSoon() {
        return ResponseEntity.ok(dashboardService.getExpiringSoon());
    }

    @Operation(summary = "Get expired purchase histories for dashboard")
    @GetMapping("/expired")
    public ResponseEntity<List<PurchaseHistoryResponseDTO>> getExpired() {
        return ResponseEntity.ok(dashboardService.getExpired());
    }

    @Operation(summary = "Get total assets count for dashboard")
    @GetMapping("/total-assets")
    public ResponseEntity<Long> getTotalAssets() {
        return ResponseEntity.ok(dashboardService.getTotalAssets());
    }

    @Operation(summary = "Get total users count for dashboard")
    @GetMapping("/total-users")
    public ResponseEntity<Long> getTotalUsers() {
        return ResponseEntity.ok(dashboardService.getTotalUsers());
    }

    @Operation(summary = "Get category-wise assets for dashboard")
    @GetMapping("/category-wise")
    public ResponseEntity<List<Map<String, Object>>> getCategoryWiseAssets() {
        return ResponseEntity.ok(dashboardService.getCategoryWiseAssets());
    }
}
