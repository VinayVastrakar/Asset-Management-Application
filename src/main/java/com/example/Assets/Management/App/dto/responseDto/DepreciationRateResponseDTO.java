package com.example.Assets.Management.App.dto.responseDto;

import lombok.Data;
import java.time.LocalDate;

import com.example.Assets.Management.App.Enums.DepreciationMethod;

@Data
public class DepreciationRateResponseDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String assetType;
    private String financialYear;
    private Double depreciationPercentage;
    private DepreciationMethod depreciationMethod;
    private Integer usefulLifeYears;
    private Double residualValuePercentage;
    private LocalDate effectiveFromDate;
    private LocalDate effectiveToDate;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}