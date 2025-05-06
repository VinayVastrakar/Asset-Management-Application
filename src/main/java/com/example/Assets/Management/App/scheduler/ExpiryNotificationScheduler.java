package com.example.Assets.Management.App.scheduler;

import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ExpiryNotificationScheduler {
    private final AssetRepository assetRepository;
    private final EmailService emailService;

    public ExpiryNotificationScheduler(AssetRepository assetRepository, EmailService emailService) {
        this.assetRepository = assetRepository;
        this.emailService = emailService;
    }

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkExpiringAssets() {
        LocalDate now = LocalDate.now();
        LocalDate soon = now.plusDays(30);
        List<Asset> expiringAssets = assetRepository.findByExpiryDateBetween(now, soon);

        for (Asset asset : expiringAssets) {
            // Send to assigned user
            String userEmail = asset.getAssignedToUser().getEmail();
            String subject = "Asset Expiry Alert: " + asset.getName();
            String text = "The asset '" + asset.getName() + "' is expiring on " + asset.getExpiryDate();
            emailService.sendEmail(userEmail, subject, text);

            // Send to admin
            // String adminEmail = "your_email@gmail.com"; // Get this from application.yml
            // String adminSubject = "Admin - Asset Expiry Alert: " + asset.getName(); 
            // String adminText = "The asset '" + asset.getName() + "' assigned to " + 
            //                  asset.getAssignedToUser().getName() + " is expiring on " + asset.getExpiryDate();
            // emailService.sendEmail(adminEmail, adminSubject, adminText);
        }
    }
}
