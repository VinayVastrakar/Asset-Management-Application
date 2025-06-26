package com.example.Assets.Management.App.dto.requestDto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

import com.example.Assets.Management.App.Enums.DepreciationMethod;

@Data
public class DepreciationRateRequestDTO {
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    private String assetType; // Optional, for specific asset types
    
    @NotBlank(message = "Financial year is required")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Financial year must be in format YYYY-YY")
    private String financialYear;
    
    @NotNull(message = "Depreciation percentage is required")
    @DecimalMin(value = "0.0", message = "Depreciation percentage must be positive")
    @DecimalMax(value = "100.0", message = "Depreciation percentage cannot exceed 100%")
    private Double depreciationPercentage;
    
    @NotBlank(message = "Depreciation method is required")
    @Pattern(regexp = "PRO_RATA|SLM|WDV", message = "Depreciation method must be either SLM or WDV")
    private DepreciationMethod depreciationMethod;
    
    @Min(value = 1, message = "Useful life must be at least 1 year")
    private Integer usefulLifeYears;    
    
    @DecimalMin(value = "0.0", message = "Residual value percentage must be positive")
    @DecimalMax(value = "100.0", message = "Residual value percentage cannot exceed 100%")
    private Double residualValuePercentage;
    
    @NotNull(message = "Effective from date is required")
    private LocalDate effectiveFromDate;
    
    private LocalDate effectiveToDate; // Optional, null means currently effective
}