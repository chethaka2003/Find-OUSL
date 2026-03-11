package com.ousl.lfs.ousl_lfs_backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    // ---------- FR1 ----------
    @Value("${ulfs.auth.verificationLinkBase}")
    private String verificationBase;

    // ---------- FR3 ----------
    @Value("${ulfs.auth.resetLinkBase}")
    private String resetBase;

    public void sendVerificationEmail(String to, String token) {
        String link = verificationBase + "?token=" +
                URLEncoder.encode(token, StandardCharsets.UTF_8);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Verify your ULFS account");
        msg.setText("""
                Hi,

                Please verify your account using the link below:
                """ + link + """

                This link expires in 60 minutes.
                """);

        safeSend(msg);
    }

    // ---------- FR3: Password Reset ----------
    public void sendPasswordResetEmail(String to, String token) {
        String link = resetBase + "?token=" +
                URLEncoder.encode(token, StandardCharsets.UTF_8);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("ULFS Password Reset");
        msg.setText("""
                Hi,

                You requested a password reset.

                Click the link below (valid for 1 hour):
                """ + link + """

                If you did not request this, please ignore this email.
                """);

        safeSend(msg);
    }

    public void sendPasswordChangeConfirmation(String to) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("ULFS Password Changed");
        msg.setText("""
                Hi,

                Your password was successfully changed.

                If this was not you, please contact support immediately.
                """);

        safeSend(msg);
    }

    private void safeSend(SimpleMailMessage msg) {
        try {
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("Email send failed: " + e.getMessage());
            // DO NOT throw -> prevents 500 error
        }
    }

    public void sendLostReportConfirmation(
            String to,
            String trackingNumber,
            String category,
            String description,
            String location,
            String lostAt
    ) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("ULFS Lost Item Report Confirmation - " + trackingNumber);
        msg.setText(
                "Hi,\n\nYour lost item report has been submitted.\n\n" +
                        "Tracking Number: " + trackingNumber + "\n" +
                        "Category: " + category + "\n" +
                        "Description: " + description + "\n" +
                        "Lost Location: " + location + "\n" +
                        "Approx. Lost Date/Time: " + lostAt + "\n\n" +
                        "Thank you,\nULFS Team"
        );

        try {
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("Lost report email send failed: " + e.getMessage());
        }
    }


}
