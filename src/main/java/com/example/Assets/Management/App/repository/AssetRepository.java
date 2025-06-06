package com.example.Assets.Management.App.repository;

import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByExpiryDateBetween(LocalDate start, LocalDate end);
    List<Asset> findByAssignedToUser(Users user);
    List<Asset> findByCategory(Category category);

    Page<Asset> findByCategoryAndStatus(Category category, String status, Pageable pageable);
    Page<Asset> findByCategory(Category category, Pageable pageable);
    Page<Asset> findByStatus(String status, Pageable pageable);
}
