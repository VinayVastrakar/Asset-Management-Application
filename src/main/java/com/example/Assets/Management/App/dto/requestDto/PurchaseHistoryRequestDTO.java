package com.example.Assets.Management.App.dto.requestDto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseHistoryRequestDTO {
    private Long assetId;
    private LocalDate purchaseDate;
    private Double amount;
    private LocalDate expiryDate;
    private String vendor;
    private String invoiceNumber;
    private Integer warrantyPeriod;
    private String notify;
    private String description;
}