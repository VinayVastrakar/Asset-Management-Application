package com.example.Assets.Management.App.repository;

import com.example.Assets.Management.App.model.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Assets.Management.App.model.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
    public List<PurchaseHistory> findByAsset(Asset asset);

    List<PurchaseHistory> findByExpiryDateBetween(LocalDate start, LocalDate end);

    Page<PurchaseHistory> findByAsset(Asset asset, Pageable pageable);
}
