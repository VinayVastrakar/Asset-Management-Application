package com.example.Assets.Management.App.dto;

import com.example.Assets.Management.App.model.PurchaseHistory;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PurchaseHistoryDTO {
    private Long id;
    private Long assetId;
    private String assetName;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private Double purchasePrice;
    private String vendorName;
    private String invoiceNumber;
    private Integer warrantyPeriod;
    private Integer qty;
    private String notify;
    private String description;
} 