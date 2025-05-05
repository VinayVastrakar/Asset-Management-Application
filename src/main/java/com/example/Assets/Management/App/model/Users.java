// src/main/java/com/example/assetmanagement/model/User.java
package com.example.Assets.Management.App.model;

import com.example.Assets.Management.App.Enums.Role;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private Role role; // "ROLE_ADMIN", "ROLE_USER"
    private String mobileNumber;
}