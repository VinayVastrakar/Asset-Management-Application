package com.example.Assets.Management.App.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {
    @Value("${twilio.account-sid}")
    public String accountSid;

    @Value("${twilio.auth-token}")
    public String authToken;

    @Value("${twilio.from-number}")
    public String fromNumber;
}
