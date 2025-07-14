package com.example.Assets.Management.App.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class    PurchaseHistory {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @Column(nullable = false)
    private Double purchasePrice;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private String vendorName;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private Integer warrantyPeriod;

    @Column(nullable = false)
    private Integer qty;

    @Column(nullable = false)
    private String notify;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    private Users lastChangeBy;

    String billUrl;
    String billPublicId;

    private Double stolenValue;
    private Double disposedValue;
}
