package com.ousl.lfs.ousl_lfs_backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service @RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${ulfs.auth.verificationLinkBase}")
    String linkBase;

    public void sendVerificationEmail(String to, String token) {
        String link = linkBase + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Verify your ULFS account");
        msg.setText("Hi,\n\nPlease verify your account using this link: " + link +
                "\n\nThis link expires in 60 minutes.");
        try {
            mailSender.send(msg);
        } catch (Exception e) {
            // For early dev: log only. Later: enqueue for retry.
            System.err.println("Email send failed: " + e.getMessage());
        }
    }
}