package com.example.Assets.Management.App.dto.responseDto;

import lombok.Data;
import java.util.List;

@Data
public class FinancialYearSummaryDTO {
    private String financialYear;
    private Double totalPurchaseValue;
    private Double totalCurrentValue;
    private Double totalDepreciation;
    private List<AssetValuationDTO> assets;
}