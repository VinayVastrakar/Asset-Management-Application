package com.example.Assets.Management.App.dto.requestDto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AssetRequestDTO {
    private String name;
    private String description;
    private Long categoryId;
    private LocalDate purchaseDate; // in format yyyy-MM-dd 
    private String notify;
    private LocalDate expiryDate; // in format yyyy-MM-dd
    private Integer warrantyPeriod; // in months
    private String status; // e.g., "active", "assigned", "retired"
}
