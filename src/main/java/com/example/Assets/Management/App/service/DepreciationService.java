package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.dto.requestDto.DepreciationRateRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.DepreciationRateResponseDTO;
import com.example.Assets.Management.App.model.DepreciationRate;
import com.example.Assets.Management.App.Enums.DepreciationMethod;
import com.example.Assets.Management.App.repository.DepreciationRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepreciationService {
    
    private final DepreciationRateRepository depreciationRateRepository;
    
    public DepreciationService(DepreciationRateRepository depreciationRateRepository) {
        this.depreciationRateRepository = depreciationRateRepository;
    }
    
    /**
     * Get financial year from a date
     * Format: YYYY-YY (e.g., 2023-24 for dates between April 2023 and March 2024)
     */
    public String getFinancialYear(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        
        // If month is April or later, it's the current financial year
        if (month >= 4) {
            return year + "-" + String.format("%02d", (year + 1) % 100);
        } else {
            return (year - 1) + "-" + String.format("%02d", year % 100);
        }
    }
    
    /**
     * Get applicable depreciation rate for a category and date
     */
    public Optional<DepreciationRate> getApplicableRate(Long categoryId, LocalDate date) {
        return depreciationRateRepository.findApplicableRateByCategoryAndDate(categoryId, date);
    }
    
    /**
     * Get applicable depreciation rate for a category, asset type and date
     */
    public Optional<DepreciationRate> getApplicableRate(Long categoryId, String assetType, LocalDate date) {
        return depreciationRateRepository.findApplicableRateByCategoryAssetTypeAndDate(categoryId, assetType, date);
    }
    
    /**
     * Calculate depreciation for an asset for a specific financial year
     */
    public double calculateDepreciation(double purchasePrice, LocalDate purchaseDate, 
                                      Long categoryId, String financialYear) {
        
        // Get the depreciation rate for the purchase date
        Optional<DepreciationRate> rateOpt = getApplicableRate(categoryId, purchaseDate);
        if (rateOpt.isEmpty()) {
            return 0.0; // No rate found, no depreciation
        }
        
        DepreciationRate rate = rateOpt.get();
        return calculateDepreciation(purchasePrice, purchaseDate, rate, financialYear);
    }
    
    /**
     * Calculate depreciation using a specific rate
     */
    public double calculateDepreciation(double purchasePrice, LocalDate purchaseDate, 
                                      DepreciationRate rate, String financialYear) {
        String purchaseFY = getFinancialYear(purchaseDate);
        String targetFY = financialYear;
        
        // If asset was purchased after the target FY, no depreciation
        if (purchaseFY.compareTo(targetFY) > 0) {
            return 0.0;
        }
        
        double currentValue = purchasePrice;
        String currentFY = purchaseFY;
        boolean isFirstYear = true;
        
        // Calculate depreciation year by year until we reach the target FY
        while (currentFY.compareTo(targetFY) <= 0) {
            double depreciation = 0.0;
            DepreciationMethod method = rate.getDepreciationMethod();
            if (DepreciationMethod.SLM.equals(method)) {
                double annualDepreciation = (purchasePrice * (1 - rate.getResidualValuePercentage() / 100)) 
                                          / rate.getUsefulLifeYears();
                depreciation = annualDepreciation;
            } else if (DepreciationMethod.WDV.equals(method)) {
                depreciation = currentValue * (rate.getDepreciationPercentage() / 100);
            } else if (DepreciationMethod.PRO_RATA.equals(method)) {
                LocalDate fyStart = LocalDate.of(Integer.parseInt(currentFY.substring(0, 4)), 4, 1);
                LocalDate fyEnd = fyStart.plusYears(1).minusDays(1);
                long daysHeld;
                if (isFirstYear) {
                    daysHeld = java.time.temporal.ChronoUnit.DAYS.between(purchaseDate, fyEnd.plusDays(1));
                } else {
                    daysHeld = java.time.temporal.ChronoUnit.DAYS.between(fyStart, fyEnd.plusDays(1));
                }
                long totalDays = fyStart.isLeapYear() ? 366 : 365;
                double annualDepreciation = (purchasePrice * (rate.getDepreciationPercentage() / 100));
                depreciation = annualDepreciation * ((double) daysHeld / totalDays);
                isFirstYear = false;
            }
            currentValue -= depreciation;
            if (currentFY.equals(targetFY)) {
                break;
            }
            String[] parts = currentFY.split("-");
            int startYear = Integer.parseInt(parts[0]);
            currentFY = (startYear + 1) + "-" + String.format("%02d", (startYear + 2) % 100);
        }
        return purchasePrice - currentValue;
    }
    
    /**
     * Get current value of an asset
     */
    public double getCurrentValue(double purchasePrice, LocalDate purchaseDate, 
                                Long categoryId, LocalDate asOfDate) {
        
        String currentFY = getFinancialYear(asOfDate);
        double totalDepreciation = calculateDepreciation(purchasePrice, purchaseDate, categoryId, currentFY);
        return purchasePrice - totalDepreciation;
    }
    
    /**
     * Get current value of an asset using a specific rate
     */
    public double getCurrentValue(double purchasePrice, LocalDate purchaseDate, 
                                DepreciationRate rate, LocalDate asOfDate) {
        
        String currentFY = getFinancialYear(asOfDate);
        double totalDepreciation = calculateDepreciation(purchasePrice, purchaseDate, rate, currentFY);
        return purchasePrice - totalDepreciation;
    }
}