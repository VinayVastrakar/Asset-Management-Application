package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.dto.responseDto.FinancialYearSummaryDTO;
import com.example.Assets.Management.App.service.AssetValuationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset-valuation")
@Tag(name = "Asset Valuation", description = "Asset Valuation APIs")
@SecurityRequirement(name = "bearerAuth")
public class AssetValuationController {
    
    private final AssetValuationService assetValuationService;
    
    public AssetValuationController(AssetValuationService assetValuationService) {
        this.assetValuationService = assetValuationService;
    }
    
    @GetMapping("/financial-year/{financialYear}")
    @Operation(summary = "Get asset valuations for a specific financial year")
    public ResponseEntity<FinancialYearSummaryDTO> getAssetValuationsForFinancialYear(
            @Parameter(description = "Financial year in format YYYY-YY", example = "2023-24")
            @PathVariable String financialYear) {
        
        FinancialYearSummaryDTO summary = assetValuationService.getAssetValuationsForFinancialYear(financialYear);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/financial-years")
    @Operation(summary = "Get asset valuations for multiple financial years")
    public ResponseEntity<List<FinancialYearSummaryDTO>> getAssetValuationsForFinancialYears(
            @Parameter(description = "List of financial years", example = "2022-23,2023-24,2024-25")
            @RequestParam List<String> financialYears) {
        
        List<FinancialYearSummaryDTO> summaries = assetValuationService.getAssetValuationsForFinancialYears(financialYears);
        return ResponseEntity.ok(summaries);
    }
    
    @GetMapping("/year-range")
    @Operation(summary = "Get asset valuations for a range of financial years")
    public ResponseEntity<List<FinancialYearSummaryDTO>> getAssetValuationsForYearRange(
            @Parameter(description = "Start financial year", example = "2022-23")
            @RequestParam String startYear,
            @Parameter(description = "End financial year", example = "2024-25")
            @RequestParam String endYear) {
        
        List<FinancialYearSummaryDTO> summaries = assetValuationService.getAssetValuationsForYearRange(startYear, endYear);
        return ResponseEntity.ok(summaries);
    }
}