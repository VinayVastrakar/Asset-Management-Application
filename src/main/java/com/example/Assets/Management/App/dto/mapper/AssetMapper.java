package com.example.Assets.Management.App.dto.mapper;

import com.example.Assets.Management.App.dto.requestDto.AssetRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.AssetResponseDTO;
import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.service.CategoryService;
import com.example.Assets.Management.App.service.UserService;

import org.springframework.stereotype.Component;

@Component
public class AssetMapper {

    private final CategoryService categoryService;
    private final UserService userService;

    public AssetMapper(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }   

    public AssetResponseDTO toResponseDTO(Asset asset) {
        AssetResponseDTO dto = new AssetResponseDTO();
        dto.setId(asset.getId());
        dto.setName(asset.getName());
        dto.setDescription(asset.getDescription());
        dto.setCategoryName(asset.getCategory().getName());
        dto.setPurchaseDate(asset.getPurchaseDate());
        dto.setExpiryDate(asset.getExpiryDate());
        dto.setImageUrl(asset.getImageUrl());
        dto.setWarrantyPeriod(asset.getWarrantyPeriod());
        dto.setStatus(asset.getStatus());
        dto.setAssignedToUserName(asset.getAssignedToUser() != null ? asset.getAssignedToUser().getName() : null);
        return dto;
    }

    public Asset toEntity(AssetRequestDTO dto) {
        Asset asset = new Asset();
        asset.setName(dto.getName());
        asset.setDescription(dto.getDescription());
        asset.setCategory(categoryService.getCategoryById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found")));
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setExpiryDate(dto.getExpiryDate());
        asset.setWarrantyPeriod(dto.getWarrantyPeriod());
        asset.setStatus(dto.getStatus());
        return asset;
    }
}
