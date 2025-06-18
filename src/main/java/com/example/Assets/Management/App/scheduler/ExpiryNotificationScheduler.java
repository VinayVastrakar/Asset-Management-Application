package com.example.Assets.Management.App.scheduler;

import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.PurchaseHistory;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.repository.PurchaseHistoryRepository;
import com.example.Assets.Management.App.repository.UserRepository;
import com.example.Assets.Management.App.service.EmailService;
import com.example.Assets.Management.App.service.SmsService;
import com.example.Assets.Management.App.Enums.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExpiryNotificationScheduler {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private UserRepository userRepository;

    // Runs every day at midnight
    @Scheduled(cron = "0 59 12 * * ?")
    public void checkExpiringAssets() {
        LocalDate now = LocalDate.now();
        LocalDate soon = now.plusDays(30);
        
        // Get all purchase histories that are expiring soon and have notifications enabled
        List<PurchaseHistory> expiringHistories = purchaseHistoryRepository.findByExpiryDateBetween(now, soon)
            .stream()
            .filter(history -> "Yes".equalsIgnoreCase(history.getNotify()))
            .collect(Collectors.toList());

        // Get admin emails for CC
        List<String> adminEmails = userRepository.findByRole(Role.ADMIN)
                                    .stream()
                                    .map(Users::getEmail)
                                    .collect(Collectors.toList());
        // for (Asset asset : expiringAssets) {
            // Send to assigned user
            // String userEmail = asset.getAssignedToUser().getEmail();
            // String subject = "Asset Expiry Alert: " + asset.getName();
            // String text = "The asset '" + asset.getName() + "' is expiring on " + asset.getExpiryDate();
            // String mobileNumber = asset.getAssignedToUser().getMobileNumber();
            // emailService.sendEmailWithCc(userEmail,adminEmails, subject, text);

        for (PurchaseHistory history : expiringHistories) {
            Asset asset = history.getAsset();
            String subject = "Asset Expiry Alert: " + asset.getName();
            String baseText = String.format(
                "The asset '%s' is expiring on %s\nPurchase Date: %s\nWarranty Period: %s months",
                asset.getName(),
                history.getExpiryDate(),
                history.getPurchaseDate(),
                history.getWarrantyPeriod()
            );

            if (asset.getAssignedToUser() != null) {
                // Case 1: Asset has assigned user - send to user with admins in CC
                String userEmail = asset.getAssignedToUser().getEmail();
                emailService.sendEmailWithCc(userEmail, adminEmails, subject, baseText);
            } else {
                // Case 2: Asset is unassigned - send only to admins
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
