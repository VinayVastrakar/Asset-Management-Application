package com.example.Assets.Management.App.service;

import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.Assets.Management.App.Enums.Role;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendEmailWithCc(String to, List<String> cc, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setCc(cc.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendEmailToMultipleRecipients(List<String> to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendWelcomeEmail(String toEmail, String name, Role role) {
        String subject = "Welcome to Asset Management App!";
        String message;

        if (role == Role.ADMIN) {
            message = "Dear Admin " + name + ",\n\nWelcome! Your administrative access has been created successfully.";
        } else {
            message = "Hello " + name + ",\n\nWelcome! Your user account has been registered successfully.";
        }

        // Simulated: Replace with actual JavaMailSender logic
        sendEmail(toEmail, subject, message);
        System.out.println("Sending email to " + toEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message:\n" + message);
    }

}
