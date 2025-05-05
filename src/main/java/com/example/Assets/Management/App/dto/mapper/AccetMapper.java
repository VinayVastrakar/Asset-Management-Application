package com.example.Assets.Management.App.dto.mapper;

import com.example.Assets.Management.App.dto.responseDto.AssetResponseDTO;
import com.example.Assets.Management.App.model.Asset;

public class AccetMapper {
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
}
