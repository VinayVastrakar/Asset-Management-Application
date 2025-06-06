package com.example.Assets.Management.App.dto.mapper;

import com.example.Assets.Management.App.dto.PurchaseHistoryDTO;
import com.example.Assets.Management.App.model.PurchaseHistory;
import org.springframework.stereotype.Component;

@Component
public class PurchaseHistoryMapper {
    
    public PurchaseHistoryDTO toDTO(PurchaseHistory entity) {
        if (entity == null) return null;
        
        PurchaseHistoryDTO dto = new PurchaseHistoryDTO();
        dto.setId(entity.getId());
        dto.setAssetId(entity.getAsset().getId());
        dto.setAssetName(entity.getAsset().getName());
        dto.setPurchaseDate(entity.getPurchaseDate());
        dto.setPurchasePrice(entity.getPurchasePrice());
        dto.setVendorName(entity.getVendorName());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setWarrantyPeriod(entity.getWarrantyPeriod());
        dto.setDescription(entity.getDescription());
        
        return dto;
    }
    
    public PurchaseHistory toEntity(PurchaseHistoryDTO dto) {
        if (dto == null) return null;
        
        return PurchaseHistory.builder()
                .id(dto.getId())
                .purchaseDate(dto.getPurchaseDate())
                .purchasePrice(dto.getPurchasePrice())
                .vendorName(dto.getVendorName())
                .invoiceNumber(dto.getInvoiceNumber())
                .warrantyPeriod(dto.getWarrantyPeriod())
                .description(dto.getDescription())
                .asset(null)
                .build();
    }
} 