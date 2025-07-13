package com.example.Assets.Management.App.repository;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.AssetAssignmentHistory;

public interface AssetAssignmentHistoryRepository extends JpaRepository<AssetAssignmentHistory, Long> {
    List<AssetAssignmentHistory> findByAsset(Asset asset);
    
    // For Excel export
    List<AssetAssignmentHistory> findByAssetId(Long assetId);
    List<AssetAssignmentHistory> findByAssignmentDateBetween(LocalDateTime start, LocalDateTime end);
    List<AssetAssignmentHistory> findByAssetIdAndAssignmentDateBetween(Long assetId, LocalDateTime start, LocalDateTime end);
    
    // Find by category ID
    @Query("SELECT h FROM AssetAssignmentHistory h WHERE h.asset.category.id = :categoryId")
    List<AssetAssignmentHistory> findByCategoryId(@Param("categoryId") Long categoryId);
    
    // Find by asset ID and category ID
    @Query("SELECT h FROM AssetAssignmentHistory h WHERE h.asset.id = :assetId AND h.asset.category.id = :categoryId")
    List<AssetAssignmentHistory> findByAssetIdAndCategoryId(@Param("assetId") Long assetId, @Param("categoryId") Long categoryId);
}
