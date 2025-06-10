// src/main/java/com/example/assetmanagement/model/Asset.java
package com.example.Assets.Management.App.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToOne
    private Category category;

    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String imageUrl;
    private String imagePublicId;
    private Integer warrantyPeriod; // in months
    private String status; // e.g., "   AVAILABLE", "ASSIGNED", "INACTIVE"
    private String notify;

    @ManyToOne
    private Users assignedToUser;

    @ManyToOne
    private Users lastModifiedBy;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<AssetAssignmentHistory> assignmentHistory = new ArrayList<>();
    
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<PurchaseHistory> purchaseHistories = new ArrayList<>();

}