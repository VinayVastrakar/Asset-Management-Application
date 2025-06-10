package com.example.Assets.Management.App.repository;

import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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



    @Query(value = """
        SELECT a.* FROM asset a 
        WHERE NOT EXISTS (
            SELECT 1 FROM purchase_history p 
            WHERE p.asset_id = a.id
        )
        /*#{#pageable}*/
        """,
        countQuery = """
        SELECT COUNT(*) FROM asset a 
        WHERE NOT EXISTS (
            SELECT 1 FROM purchase_history p 
            WHERE p.asset_id = a.id
        )
        """,
        nativeQuery = true)
    Page<Asset> findAssetsNotInPurchaseHistory(Pageable pageable);
}
