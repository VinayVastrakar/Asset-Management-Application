package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.dto.responseDto.AssetResponseDTO;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryResponseDTO;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.repository.PurchaseHistoryRepository;
import com.example.Assets.Management.App.repository.UserRepository;
import com.example.Assets.Management.App.dto.mapper.PurchaseHistoryMapper;
import com.example.Assets.Management.App.dto.mapper.AssetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final PurchaseHistoryMapper purchaseHistoryMapper;
    private final AssetMapper assetMapper;

    @Autowired
    public DashboardService(AssetRepository assetRepository, UserRepository userRepository,
                           PurchaseHistoryRepository purchaseHistoryRepository,
                           PurchaseHistoryMapper purchaseHistoryMapper,
                           AssetMapper assetMapper) {
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.purchaseHistoryMapper = purchaseHistoryMapper;
        this.assetMapper = assetMapper;
    }

    public List<AssetResponseDTO> getAllAssets() {
        return assetRepository.findAll().stream().map(assetMapper::toResponseDTO).collect(Collectors.toList());
    }

    public List<AssetResponseDTO> getAssignedAssets() {
        return assetRepository.findAll().stream()
                .filter(asset -> asset.getAssignedToUser() != null)
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AssetResponseDTO> getNonAssignedAssets() {
        return assetRepository.findAll().stream()
                .filter(asset -> asset.getAssignedToUser() == null)
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PurchaseHistoryResponseDTO> getExpiringSoon() {
        LocalDate now = LocalDate.now();
        LocalDate oneMonthFromNow = now.plusMonths(1);
        return purchaseHistoryRepository.findByExpiryDateBetween(now, oneMonthFromNow)
                .stream()
                .filter(history -> "Yes".equalsIgnoreCase(history.getNotify()))
                .map(purchaseHistoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PurchaseHistoryResponseDTO> getExpired() {
        LocalDate now = LocalDate.now();
        return purchaseHistoryRepository.findByExpiryDateBefore(now)
                .stream()
                .filter(history -> "Yes".equalsIgnoreCase(history.getNotify()))
                .map(purchaseHistoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public long getTotalAssets() {
        return assetRepository.count();
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public List<Map<String, Object>> getCategoryWiseAssets() {
        List<AssetResponseDTO> assets = getAllAssets();
        Map<String, Long> categoryCount = assets.stream()
                .collect(Collectors.groupingBy(
                        AssetResponseDTO::getCategoryName,
                        Collectors.counting()
                ));
        return categoryCount.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> category = new HashMap<>();
                    category.put("category", entry.getKey());
                    category.put("count", entry.getValue());
                    return category;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDashboardStats() {
        long totalAssets = getTotalAssets();
        long totalUsers = getTotalUsers();
        List<Map<String, Object>> categoryWise = getCategoryWiseAssets();
        long expiringSoonCount = getExpiringSoon().size();
        long expiredAssetCount = getExpired().size();
        long assignedAssetCount = getAssignedAssets().size();
        long nonAssignedAssetCount = getNonAssignedAssets().size();

        Map<String, Object> data = new HashMap<>();
        data.put("totalAssets", totalAssets);
        data.put("totalUsers", totalUsers);
        data.put("categoryWise", categoryWise);
        data.put("expiringSoonCount", expiringSoonCount);
        data.put("expiredAssets", expiredAssetCount);
        data.put("assignedAssets", assignedAssetCount);
        data.put("nonAssignedAssets", nonAssignedAssetCount);

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("message", "Dashboard statistics retrieved successfully");
        response.put("status", 200);
        return response;
    }
} 