package com.example.Assets.Management.App.config;

import org.springframework.context.annotation.Configuration;
import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class TwilioConfig {
    private final String accountSid;
    private final String authToken;
    private final String fromNumber;

    public TwilioConfig() {
        Dotenv dotenv = Dotenv.load();
        accountSid = dotenv.get("TWILIO_ACCOUNT_SID");
        authToken = dotenv.get("TWILIO_AUTH_TOKEN");
        fromNumber = dotenv.get("TWILIO_FROM_NUMBER");
    }

    public String getAccountSid() {
        return accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getFromNumber() {
        return fromNumber;
    }

}
