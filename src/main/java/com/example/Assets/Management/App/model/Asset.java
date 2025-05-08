// src/main/java/com/example/assetmanagement/model/Asset.java
package com.example.Assets.Management.App.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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
    private String status; // e.g., "active", "assigned", "retired"

    @ManyToOne
    private Users assignedToUser;
}