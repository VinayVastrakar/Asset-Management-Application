package com.example.Assets.Management.App.repository;

import com.example.Assets.Management.App.model.PurchaseHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
    Page<PurchaseHistory> findByAssetId(Long assetId, Pageable pageable);
    List<PurchaseHistory> findByAssetId(Long assetId);
    List<PurchaseHistory> findByExpiryDateBefore(LocalDate date);
    List<PurchaseHistory> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);
    boolean existsByAssetIdAndInvoiceNumber(Long assetId, String invoiceNumber);
}
