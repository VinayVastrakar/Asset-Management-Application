package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.dto.responseDto.AssetValuationDTO;
import com.example.Assets.Management.App.dto.responseDto.FinancialYearSummaryDTO;
import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.PurchaseHistory;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.repository.PurchaseHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssetValuationService {
    
    private final AssetRepository assetRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final DepreciationService depreciationService;
    
    public AssetValuationService(AssetRepository assetRepository, 
                                PurchaseHistoryRepository purchaseHistoryRepository,
                                DepreciationService depreciationService) {
        this.assetRepository = assetRepository;
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.depreciationService = depreciationService;
    }
    
    /**
     * Get asset valuations for a specific financial year
     */
    public FinancialYearSummaryDTO getAssetValuationsForFinancialYear(String financialYear) {
        // Get all assets with their purchase histories
        List<Asset> assets = assetRepository.findAll();
        List<AssetValuationDTO> assetValuations = new ArrayList<>();
        
        double totalPurchaseValue = 0.0;
        double totalCurrentValue = 0.0;
        double totalDepreciation = 0.0;
        
        for (Asset asset : assets) {
            // Get the latest purchase history for this asset
            List<PurchaseHistory> purchaseHistories = purchaseHistoryRepository.findByAssetId(asset.getId());
            if (!purchaseHistories.isEmpty()) {
                PurchaseHistory latestPurchase = purchaseHistories.get(0); // Assuming sorted by date desc
                
                AssetValuationDTO valuation = calculateAssetValuation(asset, latestPurchase, financialYear);
                assetValuations.add(valuation);
                
                // Add to totals
                totalPurchaseValue += valuation.getPurchasePrice();
                totalCurrentValue += valuation.getCurrentValue();
                totalDepreciation += valuation.getTotalDepreciation();
            }
        }
        
        FinancialYearSummaryDTO summary = new FinancialYearSummaryDTO();
        summary.setFinancialYear(financialYear);
        summary.setTotalPurchaseValue(totalPurchaseValue);
        summary.setTotalCurrentValue(totalCurrentValue);
        summary.setTotalDepreciation(totalDepreciation);
        summary.setAssets(assetValuations);
        
        return summary;
    }
    
    /**
     * Get asset valuations for multiple financial years
     */
    public List<FinancialYearSummaryDTO> getAssetValuationsForFinancialYears(List<String> financialYears) {
        return financialYears.stream()
                .map(this::getAssetValuationsForFinancialYear)
                .collect(Collectors.toList());
    }
    
    /**
     * Get asset valuations for a range of financial years
     */
    public List<FinancialYearSummaryDTO> getAssetValuationsForYearRange(String startYear, String endYear) {
        List<String> years = generateFinancialYearRange(startYear, endYear);
        return getAssetValuationsForFinancialYears(years);
    }
    
    /**
     * Calculate valuation for a single asset
     */
    private AssetValuationDTO calculateAssetValuation(Asset asset, PurchaseHistory purchaseHistory, String financialYear) {
        AssetValuationDTO valuation = new AssetValuationDTO();
        
        valuation.setAssetId(asset.getId());
        valuation.setAssetName(asset.getName());
        valuation.setCategoryName(asset.getCategory().getName());
        valuation.setPurchaseDate(purchaseHistory.getPurchaseDate());
        valuation.setPurchaseFinancialYear(depreciationService.getFinancialYear(purchaseHistory.getPurchaseDate()));
        valuation.setPurchasePrice(purchaseHistory.getPurchasePrice());
        
        // Calculate current value and depreciation
        double currentValue = depreciationService.getCurrentValue(
            purchaseHistory.getPurchasePrice(), 
            purchaseHistory.getPurchaseDate(), 
            asset.getCategory().getId(), 
            getFinancialYearEndDate(financialYear)
        );
        
        double totalDepreciation = purchaseHistory.getPurchasePrice() - currentValue;
        
        valuation.setCurrentValue(currentValue);
        valuation.setTotalDepreciation(totalDepreciation);
        
        // Calculate depreciation for this specific year
        double depreciationThisYear = depreciationService.calculateDepreciation(
            purchaseHistory.getPurchasePrice(),
            purchaseHistory.getPurchaseDate(),
            asset.getCategory().getId(),
            getFinancialYearEndDate(financialYear)
        );
        valuation.setDepreciationThisYear(depreciationThisYear);
        
        // Get depreciation method and rate
        var rateOpt = depreciationService.getApplicableRate(
            asset.getCategory().getId(), 
            purchaseHistory.getPurchaseDate()
        );
        
        if (rateOpt.isPresent()) {
            var rate = rateOpt.get();
            valuation.setDepreciationMethod(rate.getDepreciationMethod());
            valuation.setDepreciationRate(rate.getDepreciationPercentage());
        }
        
        return valuation;
    }
    
    /**
     * Generate a list of financial years between start and end
     */
    private List<String> generateFinancialYearRange(String startYear, String endYear) {
        List<String> years = new ArrayList<>();
        
        String[] startParts = startYear.split("-");
        String[] endParts = endYear.split("-");
        
        int startFY = Integer.parseInt(startParts[0]);
        int endFY = Integer.parseInt(endParts[0]);
        
        for (int year = startFY; year <= endFY; year++) {
            years.add(year + "-" + String.format("%02d", (year + 1) % 100));
        }
        
        return years;
    }
    
    /**
     * Get the end date of a financial year (March 31st)
     */
    private LocalDate getFinancialYearEndDate(String financialYear) {
        String[] parts = financialYear.split("-");
        int year = Integer.parseInt(parts[0]);
        return LocalDate.of(year + 1, 3, 31);
    }

    public double getTotalPurchaseValue() {
        return purchaseHistoryRepository.findAll().stream()
            .mapToDouble(PurchaseHistory::getPurchasePrice)
            .sum();
    }

    public double getTotalLatestPurchaseValue() {
        Map<Long, Optional<PurchaseHistory>> latestByAsset = purchaseHistoryRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                ph -> ph.getAsset().getId(),
                Collectors.maxBy(Comparator.comparing(PurchaseHistory::getPurchaseDate))
            ));
        return latestByAsset.values().stream()
            .filter(Optional::isPresent)
            .mapToDouble(opt -> opt.get().getPurchasePrice())
            .sum();
    }
}