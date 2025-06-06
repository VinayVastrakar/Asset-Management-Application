package com.example.Assets.Management.App.scheduler;

import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.repository.UserRepository;
import com.example.Assets.Management.App.service.EmailService;
import com.example.Assets.Management.App.service.SmsService;
import com.example.Assets.Management.App.Enums.Role;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class ExpiryNotificationScheduler {
    private final AssetRepository assetRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final UserRepository userRepository;

    public ExpiryNotificationScheduler(AssetRepository assetRepository, EmailService emailService,SmsService smsService, UserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.emailService = emailService;
        this.smsService = smsService;
        this.userRepository = userRepository;
    }

    // Runs every day at midnight
    @Scheduled(cron = "0 18 15 * * ?")
    public void checkExpiringAssets() {
        LocalDate now = LocalDate.now();
        LocalDate soon = now.plusDays(30);
        List<Asset> expiringAssets = assetRepository.findByExpiryDateBetween(now, soon);
        List<String> adminEmails = userRepository.findByRole(Role.ADMIN)
                                    .stream()
                                    .map(Users::getEmail)
                                    .collect(Collectors.toList());
        for (Asset asset : expiringAssets) {
            // Send to assigned user
            // String userEmail = asset.getAssignedToUser().getEmail();
            // String subject = "Asset Expiry Alert: " + asset.getName();
            // String text = "The asset '" + asset.getName() + "' is expiring on " + asset.getExpiryDate();
            // String mobileNumber = asset.getAssignedToUser().getMobileNumber();
            // emailService.sendEmailWithCc(userEmail,adminEmails, subject, text);

            String subject = "Asset Expiry Alert: " + asset.getName();
            String baseText = "The asset '" + asset.getName() + "' is expiring on " + asset.getExpiryDate();
            if (asset.getAssignedToUser() != null) {
                // Case 1: Asset has assigned user - send to user with admins in CC
                String userEmail = asset.getAssignedToUser().getEmail();
                String text = baseText;
                emailService.sendEmailWithCc(userEmail, adminEmails, subject, text);
            }
            else{
                String adminText = baseText + "\n\nNote: This asset is currently unassigned.";
                String adminSubject = "[Unassigned] " + subject;
                emailService.sendEmailToMultipleRecipients(adminEmails, adminSubject, adminText);
            }
            // Send to admin
            // String adminEmail = "your_email@gmail.com"; // Get this from application.yml
            // String adminSubject = "Admin - Asset Expiry Alert: " + asset.getName(); 
            // String adminText = "The asset '" + asset.getName() + "' assigned to " + 
            //                  asset.getAssignedToUser().getName() + " is expiring on " + asset.getExpiryDate();
            // emailService.sendEmail(adminEmail, adminSubject, adminText);

            // smsService.sendSms(mobileNumber, text);
        }
    }
}
