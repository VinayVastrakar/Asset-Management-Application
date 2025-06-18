package com.example.Assets.Management.App.dto.requestDto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AssetRequestDTO {
    private String name;
    private String description;
    private Long categoryId;
    private Integer warrantyPeriod; // in months
    private String status; // e.g., "active", "assigned", "retired"
}
