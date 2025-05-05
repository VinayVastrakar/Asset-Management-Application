package com.example.Assets.Management.App.dto.responseDto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class AssetResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String imageUrl;
    private Integer warrantyPeriod; // in months
    private String status;
    private String assignedToUserName;
}

