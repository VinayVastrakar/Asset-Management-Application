package com.example.Assets.Management.App.repository;

import com.example.Assets.Management.App.model.DepreciationRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepreciationRateRepository extends JpaRepository<DepreciationRate, Long> {
    
    // Find rate by category and financial year
    Optional<DepreciationRate> findByCategoryIdAndFinancialYear(Long categoryId, String financialYear);
    
    // Find rate by category, asset type and financial year
    Optional<DepreciationRate> findByCategoryIdAndAssetTypeAndFinancialYear(
        Long categoryId, String assetType, String financialYear);
    
    // Find all rates for a category
    List<DepreciationRate> findByCategoryId(Long categoryId);
    
    // Find all rates for a financial year
    List<DepreciationRate> findByFinancialYear(String financialYear);
    
    // Find applicable rate for a category on a specific date
    @Query("SELECT dr FROM DepreciationRate dr WHERE dr.category.id = :categoryId " +
           "AND dr.effectiveFromDate <= :date " +
           "AND (dr.effectiveToDate IS NULL OR dr.effectiveToDate >= :date) " +
           "ORDER BY dr.effectiveFromDate DESC")
    Optional<DepreciationRate> findApplicableRateByCategoryAndDate(
        @Param("categoryId") Long categoryId, @Param("date") LocalDate date);
    
    // Find applicable rate for a category and asset type on a specific date
    @Query("SELECT dr FROM DepreciationRate dr WHERE dr.category.id = :categoryId " +
           "AND dr.assetType = :assetType " +
           "AND dr.effectiveFromDate <= :date " +
           "AND (dr.effectiveToDate IS NULL OR dr.effectiveToDate >= :date) " +
           "ORDER BY dr.effectiveFromDate DESC")
    Optional<DepreciationRate> findApplicableRateByCategoryAssetTypeAndDate(
        @Param("categoryId") Long categoryId, @Param("assetType") String assetType, 
        @Param("date") LocalDate date);
}