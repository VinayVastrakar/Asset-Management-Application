package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.model.OtpToken;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.OtpTokenRepository;
import com.example.Assets.Management.App.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;


    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpToken otpToken = new OtpToken();
        otpToken.setEmail(email);
        otpToken.setOtp(otp);
        otpToken.setExpiry(LocalDateTime.now().plusMinutes(5));
        otpToken.setUsed(false);

        otpTokenRepository.save(otpToken);
        String userEmail = otpToken.getEmail();
        Users users = userRepository.findByEmail(userEmail).get();
        String subject = "Forget password";
        String msg = "Dear "+users.getName()+" don't share otp anyone. otp-"+otp;
        String mobileNumber = users.getMobileNumber();

        emailService.sendEmail(userEmail,subject,msg);
        smsService.sendSms(mobileNumber,msg);

        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        Optional<OtpToken> optionalOtp = otpTokenRepository.findTopByEmailAndUsedFalseOrderByExpiryDesc(email);

        if (optionalOtp.isEmpty()) return false;

        OtpToken token = optionalOtp.get();

        if (!token.getOtp().equals(otp) || token.getExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        token.setUsed(true); // Mark OTP as used
        otpTokenRepository.save(token);
        return true;
    }

}
