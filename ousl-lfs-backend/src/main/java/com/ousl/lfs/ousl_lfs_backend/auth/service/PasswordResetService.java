package com.ousl.lfs.ousl_lfs_backend.auth.service;

import com.ousl.lfs.ousl_lfs_backend.auth.dto.ForgotPasswordRequest;
import com.ousl.lfs.ousl_lfs_backend.auth.dto.ResetPasswordRequest;
import com.ousl.lfs.ousl_lfs_backend.common.util.PasswordPolicy;
import com.ousl.lfs.ousl_lfs_backend.user.model.PasswordResetToken;
import com.ousl.lfs.ousl_lfs_backend.user.repo.PasswordResetTokenRepository;
import com.ousl.lfs.ousl_lfs_backend.user.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder encoder;
    private final PasswordPolicy passwordPolicy;
    private final MailService mail;

    private static final int MAX_FAILED_ATTEMPTS = 3;

    @Transactional
    public void requestReset(ForgotPasswordRequest req) {
        users.findByEmail(req.email().toLowerCase())
                .ifPresent(user -> {
                    PasswordResetToken token = new PasswordResetToken();
                    token.setToken(UUID.randomUUID().toString());
                    token.setUser(user);
                    token.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));
                    tokens.save(token);
                    mail.sendPasswordResetEmail(user.getEmail(), token.getToken());
                });
        // Always return success → prevent email enumeration
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        PasswordResetToken token = tokens.findByToken(req.token())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid reset link"));

        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Reset link expired or already used");
        }

        if (token.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
            throw new ResponseStatusException(
                    HttpStatus.LOCKED, "Reset token locked due to multiple failures");
        }

        passwordPolicy.validate(req.newPassword());

        var user = token.getUser();
        user.setPasswordHash(encoder.encode(req.newPassword()));

        token.setUsed(true);
        users.save(user);
        tokens.save(token);

        mail.sendPasswordChangeConfirmation(user.getEmail());
    }
}
