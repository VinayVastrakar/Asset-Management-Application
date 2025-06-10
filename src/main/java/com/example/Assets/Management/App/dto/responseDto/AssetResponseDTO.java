package com.example.Assets.Management.App.dto.responseDto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String imageUrl;
    private String notify;
    private Integer warrantyPeriod; // in months
    private String status;
    private String assignedToUserName;
}

