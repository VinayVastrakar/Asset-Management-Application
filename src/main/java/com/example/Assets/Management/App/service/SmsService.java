package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.Enums.Role;
import com.example.Assets.Management.App.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    private final TwilioConfig twilioConfig;

    public SmsService(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }

    public void sendSms(String to, String body) {
        Message.creator(
                new com.twilio.type.PhoneNumber(to),
                new com.twilio.type.PhoneNumber(twilioConfig.getFromNumber()),
                body
        ).create();
    }

    public void sendWelcomeSms(String phoneNumber, String name, Role role) {
        String message;

        if (role == Role.ADMIN) {
            message = "Hi Admin " + name + ", your admin account is now active.";
        } else {
            message = "Hi " + name + ", welcome! Your account is now active.";
        }

        // Simulated: Replace with actual SMS provider integration
        sendSms(phoneNumber, message);
        System.out.println("Sending SMS to " + phoneNumber + ": " + message);
    }
}
