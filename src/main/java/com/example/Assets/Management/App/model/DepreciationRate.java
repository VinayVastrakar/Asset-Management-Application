package com.example.Assets.Management.App.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

import com.example.Assets.Management.App.Enums.DepreciationMethod;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "depreciation_rates")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepreciationRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(name = "asset_type")
    String assetType; // e.g., "Laptop", "Desktop", "Furniture"

    @Column(name = "financial_year", nullable = false)
    String financialYear; // e.g., "2023-24"

    @Column(name = "depreciation_percentage", nullable = false)
    Double depreciationPercentage;

    @Column(name = "depreciation_method", nullable = false)
    @Enumerated(EnumType.STRING)
    DepreciationMethod depreciationMethod; // "SLM" or "WDV"

    @Column(name = "useful_life_years")
    Integer usefulLifeYears;

    @Column(name = "residual_value_percentage")
    Double residualValuePercentage;

    @Column(name = "effective_from_date", nullable = false)
    LocalDate effectiveFromDate;

    @Column(name = "effective_to_date")
    LocalDate effectiveToDate;

    @Column(name = "created_at")
    LocalDate createdAt;

    @Column(name = "updated_at")
    LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}