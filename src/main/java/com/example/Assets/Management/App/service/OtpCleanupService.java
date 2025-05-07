package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.model.OtpToken;
import com.example.Assets.Management.App.repository.OtpTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OtpCleanupService {

    @Autowired
    private OtpTokenRepository otpTokenRepository;


    // Runs every day at midnight
//    @Scheduled(cron = "0 0 0 * * *")
//    public void cleanExpiredOtps() {
//        List<OtpToken> expiredTokens = otpTokenRepository.findAll().stream()
//                .filter(otp -> otp.getExpiry().isBefore(LocalDateTime.now()))
//                .toList();
//
//        otpTokenRepository.deleteAll(expiredTokens);
//        System.out.println("Cleaned up " + expiredTokens.size() + " expired OTPs");
//    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanExpiredOtps() {
        otpTokenRepository.deleteByExpiryBefore(LocalDateTime.now());
    }


}
