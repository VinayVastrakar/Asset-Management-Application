// src/main/java/com/example/assetmanagement/model/Category.java
package com.example.Assets.Management.App.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Electronics, Furniture, etc.
}