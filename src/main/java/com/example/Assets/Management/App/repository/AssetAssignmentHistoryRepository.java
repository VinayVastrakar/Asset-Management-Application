package com.example.Assets.Management.App.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.AssetAssignmentHistory;

public interface AssetAssignmentHistoryRepository extends JpaRepository<AssetAssignmentHistory, Long> {
    List<AssetAssignmentHistory> findByAsset(Asset asset);
}
