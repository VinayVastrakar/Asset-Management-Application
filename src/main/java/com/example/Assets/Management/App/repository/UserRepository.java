package com.example.Assets.Management.App.repository;

import com.example.Assets.Management.App.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    Page<Users> findAll(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM users where status='Active' ORDER BY name")
    List<Users> findAllActiveUsers();

}