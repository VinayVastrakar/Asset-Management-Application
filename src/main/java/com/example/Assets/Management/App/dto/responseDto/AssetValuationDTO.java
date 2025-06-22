package com.example.Assets.Management.App.dto.responseDto;

import lombok.Data;
import java.time.LocalDate;

import com.example.Assets.Management.App.Enums.DepreciationMethod;

@Data
public class AssetValuationDTO {
    private Long assetId;
    private String assetName;
    private String categoryName;
    private LocalDate purchaseDate;
    private String purchaseFinancialYear;
    private Double purchasePrice;
    private Double currentValue;
    private Double totalDepreciation;
    private Double depreciationThisYear;
    private DepreciationMethod depreciationMethod;
    private Double depreciationRate;
}