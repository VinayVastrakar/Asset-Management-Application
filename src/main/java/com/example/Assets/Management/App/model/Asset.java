// src/main/java/com/example/assetmanagement/model/Asset.java
package com.example.Assets.Management.App.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Asset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    String description;

    @ManyToOne
    Category category;

    String imageUrl;
    String imagePublicId;
    Integer warrantyPeriod; // in months
    String status; // e.g., "   AVAILABLE", "ASSIGNED", "INACTIVE"

    @ManyToOne
    Users assignedToUser;

    @ManyToOne
    Users lastModifiedBy;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    List<AssetAssignmentHistory> assignmentHistory = new ArrayList<>();
    
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    List<PurchaseHistory> purchaseHistories = new ArrayList<>();

}