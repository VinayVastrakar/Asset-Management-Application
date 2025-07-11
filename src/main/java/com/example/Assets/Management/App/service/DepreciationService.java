package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.model.DepreciationRate;
import com.example.Assets.Management.App.Enums.DepreciationMethod;
import com.example.Assets.Management.App.repository.DepreciationRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

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
     * Get the start date of a financial year from its string representation.
     * For example, "2023-24" returns "2023-04-01".
     */
    public LocalDate getFinancialYearStartDate(String financialYear) {
        int year = Integer.parseInt(financialYear.substring(0, 4));
        return LocalDate.of(year, 4, 1);
    }

    /**
     * Get the next financial year string.
     * For example, "2023-24" returns "2024-25".
     */
    public String nextFinancialYear(String financialYear) {
        int year = Integer.parseInt(financialYear.substring(0, 4));
        return (year + 1) + "-" + String.format("%02d", (year + 2) % 100);
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
                                      Long categoryId, LocalDate asOfDate) {
        // Validate input dates
        if (asOfDate.isBefore(purchaseDate)) {
            return 0.0; // No depreciation if asOfDate is before purchase
        }

        double currentValue = purchasePrice;
        LocalDate currentDate = purchaseDate;

        // Get all applicable depreciation rates for the period 
        List<DepreciationRate> depreciationRates = depreciationRateRepository.findByCategoryId(categoryId)
            .stream()
            .filter(rate -> !rate.getEffectiveToDate().isBefore(purchaseDate)) // Rates ending on or after purchase
            .filter(rate -> !rate.getEffectiveFromDate().isAfter(asOfDate)) // Rates starting on or before asOfDate
            .sorted(Comparator.comparing(DepreciationRate::getEffectiveFromDate))
            .collect(Collectors.toList());

        // Rest of the method remains the same...
        if (depreciationRates.isEmpty()) {
            return 0.0;
        }

        for (DepreciationRate rate : depreciationRates) {
            if (!DepreciationMethod.PRO_RATA.equals(rate.getDepreciationMethod())) {
                continue;
            }

            LocalDate rateStart = rate.getEffectiveFromDate();
            LocalDate rateEnd = rate.getEffectiveToDate();
            
            LocalDate periodStart = currentDate.isAfter(rateStart) ? currentDate : rateStart;
            LocalDate periodEnd = asOfDate.isBefore(rateEnd) ? asOfDate : rateEnd;
            
            if (periodStart.isAfter(periodEnd)) {
                continue;
            }

            long daysInPeriod = ChronoUnit.DAYS.between(periodStart, periodEnd.plusDays(1));
            long daysInYear = periodStart.isLeapYear() ? 366 : 365;

            double annualDepreciation = currentValue * (rate.getDepreciationPercentage() / 100);
            double periodDepreciation = annualDepreciation * daysInPeriod / daysInYear;
            
            currentValue -= periodDepreciation;
            currentDate = periodEnd.plusDays(1);
            if (currentDate.isAfter(asOfDate)) {
                break;
            }
        }
        return purchasePrice - currentValue;
    }
    
    /**
     * Get current value of an asset
     */
    public double getCurrentValue(double purchasePrice, LocalDate purchaseDate, 
                                Long categoryId, LocalDate asOfDate) {
        
        double totalDepreciation = calculateDepreciation(purchasePrice, purchaseDate, categoryId, asOfDate);
        return purchasePrice - totalDepreciation;
    }
}