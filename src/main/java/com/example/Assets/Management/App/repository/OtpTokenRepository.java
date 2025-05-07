package com.example.Assets.Management.App.repository;

import com.example.Assets.Management.App.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findTopByEmailAndUsedFalseOrderByExpiryDesc(String email);

    void deleteByExpiryBefore(LocalDateTime dateTime);
}
