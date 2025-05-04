package com.example.Assets.Management.App.repository;

import com.example.Assets.Management.App.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByExpiryDateBetween(LocalDate start, LocalDate end);
}
