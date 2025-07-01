package com.example.Assets.Management.App.dto.mapper;

import com.example.Assets.Management.App.dto.PurchaseHistoryDTO;
import com.example.Assets.Management.App.dto.requestDto.PurchaseHistoryRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryResponseDTO;
import com.example.Assets.Management.App.model.PurchaseHistory;
import org.springframework.stereotype.Component;

@Component
public class PurchaseHistoryMapper {
    
    // Convert Entity to regular DTO
    public PurchaseHistoryDTO toDTO(PurchaseHistory entity) {
        if (entity == null) return null;
        
        PurchaseHistoryDTO dto = new PurchaseHistoryDTO();
        dto.setId(entity.getId());
        dto.setAssetId(entity.getAsset().getId());
        dto.setAssetName(entity.getAsset().getName());
        dto.setPurchaseDate(entity.getPurchaseDate());
        dto.setPurchasePrice(entity.getPurchasePrice());
        dto.setExpiryDate(entity.getExpiryDate());
        dto.setNotify(entity.getNotify());
        dto.setQty(entity.getQty());
        dto.setVendorName(entity.getVendorName());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setWarrantyPeriod(entity.getWarrantyPeriod());
        dto.setDescription(entity.getDescription());
        
        return dto;
    }
    
    // Convert regular DTO to Entity
    public PurchaseHistory toEntity(PurchaseHistoryDTO dto) {
        if (dto == null) return null;
        
        return PurchaseHistory.builder()
                .purchaseDate(dto.getPurchaseDate())
                .purchasePrice(dto.getPurchasePrice())
                .vendorName(dto.getVendorName())
                .expiryDate(dto.getExpiryDate())
                .notify(dto.getNotify())
                .qty(dto.getQty())
                .invoiceNumber(dto.getInvoiceNumber())
                .warrantyPeriod(dto.getWarrantyPeriod())
                .description(dto.getDescription())
                .build();
    }
    
    // Convert RequestDTO to Entity
    public PurchaseHistory fromRequestDTO(PurchaseHistoryRequestDTO requestDto) {
        if (requestDto == null) return null;
        
        return PurchaseHistory.builder()
                .purchaseDate(requestDto.getPurchaseDate())
                .purchasePrice(requestDto.getAmount())  // Note: mapping 'amount' to 'purchasePrice'
                .vendorName(requestDto.getVendor())     // Note: mapping 'vendor' to 'vendorName'
                .invoiceNumber(requestDto.getInvoiceNumber())
                .warrantyPeriod(requestDto.getWarrantyPeriod())
                .expiryDate(requestDto.getExpiryDate())
                .qty(requestDto.getQty())
                .notify(requestDto.getNotify())
                .description(requestDto.getDescription())
                .build();
    }
    
    // Convert Entity to ResponseDTO
    public PurchaseHistoryResponseDTO toResponseDTO(PurchaseHistory entity) {
        if (entity == null) return null;
        
        return PurchaseHistoryResponseDTO.builder()
                .id(entity.getId())
                .assetId(entity.getAsset().getId())
                .assetName(entity.getAsset().getName())
                .purchaseDate(entity.getPurchaseDate())
                .invoiceNumber(entity.getInvoiceNumber())
                .amount(entity.getPurchasePrice()) 
                .description(entity.getDescription())   // Note: mapping 'purchasePrice' to 'amount'
                .warrantyPeriod(entity.getWarrantyPeriod())
                .qty(entity.getQty())
                .billUrl(entity.getBillUrl())
                .expiryDate(entity.getExpiryDate())
                .notify(entity.getNotify())
                .vendor(entity.getVendorName())         // Note: mapping 'vendorName' to 'vendor'
                .build();
    }
}