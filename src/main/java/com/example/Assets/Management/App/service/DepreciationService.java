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
        String purchaseFY = getFinancialYear(purchaseDate);
        String targetFY = getFinancialYear(asOfDate);
        
        double currentValue = purchasePrice;
        String currentFY = purchaseFY;
        boolean isFirstYear = true;

        List<DepreciationRate> depreciationRates = depreciationRateRepository.findByCategoryId(categoryId).stream()
        .filter(rate->{
            LocalDate rateStart = rate.getEffectiveFromDate();
            LocalDate rateEnd = rate.getEffectiveToDate();
            return !(rateEnd.isBefore(purchaseDate)|| rateStart.isAfter(asOfDate));
        }).collect(Collectors.toList());
        System.out.println("Repreaciation rates ---------"+depreciationRates);
        System.out.println("Repreaciation size ---------"+depreciationRates.size());
        long daysHeld;
        for (DepreciationRate rate : depreciationRates) {
            long totalDays = rate.getEffectiveFromDate().isLeapYear() ? 366 : 365;
            if(rate.getDepreciationMethod().equals(DepreciationMethod.PRO_RATA)){
                double annualDepreciation = (purchasePrice * (rate.getDepreciationPercentage() / 100));
                if(purchaseDate.isAfter(rate.getEffectiveFromDate()) && rate.getEffectiveToDate().isBefore(asOfDate)){
                    daysHeld = ChronoUnit.DAYS.between(purchaseDate, asOfDate.plusDays(1));
                }
                

            }
        }

        // while (currentFY.compareTo(targetFY) <= 0) {
        //     // Fetch the rate for this year, not just the purchase date!
        //     Optional<DepreciationRate> rateOpt = getApplicableRate(categoryId, getFinancialYearStartDate(currentFY));
        //     if (rateOpt.isEmpty()) break; // Or handle as needed

        //     DepreciationRate rate = rateOpt.get();
        //     double depreciation = 0.0;

        //     if (DepreciationMethod.SLM.equals(rate.getDepreciationMethod())) {
        //         double annualDepreciation = (purchasePrice * (1 - rate.getResidualValuePercentage() / 100)) 
        //                                   / rate.getUsefulLifeYears();
        //         if (isFirstYear) {
        //             LocalDate fyStart = getFinancialYearStartDate(currentFY);
        //             LocalDate fyEnd = fyStart.plusYears(1).minusDays(1);
        //             long daysHeld = ChronoUnit.DAYS.between(purchaseDate, asOfDate.plusDays(1));
        //             long totalDays = fyStart.isLeapYear() ? 366 : 365;
        //             depreciation = annualDepreciation * ((double) daysHeld / totalDays);
        //         } else {
        //             depreciation = annualDepreciation;
        //         }
        //     } else if (DepreciationMethod.WDV.equals(rate.getDepreciationMethod())) {
        //         depreciation = currentValue * (rate.getDepreciationPercentage() / 100);
        //     } else if (DepreciationMethod.PRO_RATA.equals(rate.getDepreciationMethod())) {
        //         LocalDate fyStart = getFinancialYearStartDate(currentFY);
        //         LocalDate fyEnd = fyStart.plusYears(1).minusDays(1);
        //         long daysHeld;
        //         if (isFirstYear) {
        //             daysHeld = java.time.temporal.ChronoUnit.DAYS.between(purchaseDate, asOfDate.plusDays(1));
        //         } else {
        //             daysHeld = java.time.temporal.ChronoUnit.DAYS.between(fyStart, fyEnd.plusDays(1));
        //         }
        //         System.out.println("daysHeld: " + daysHeld);
        //         long totalDays = fyStart.isLeapYear() ? 366 : 365;
        //         double annualDepreciation = (purchasePrice * (rate.getDepreciationPercentage() / 100));
        //         depreciation = annualDepreciation * ((double) daysHeld / totalDays);
        //         isFirstYear = false;
        //     }
        //     currentValue -= depreciation;

        //     if (currentFY.equals(targetFY)) break;
        //     currentFY = nextFinancialYear(currentFY);
        //     isFirstYear = false;
        // }
        // return purchasePrice - currentValue;
        return purchasePrice-currentValue;
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