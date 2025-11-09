package com.ousl.lfs.ousl_lfs_backend.auth.service;


import com.ousl.lfs.ousl_lfs_backend.auth.dto.RegisterRequest;
import com.ousl.lfs.ousl_lfs_backend.common.util.PasswordPolicy;
import com.ousl.lfs.ousl_lfs_backend.common.util.UniversityDomainChecker;
import com.ousl.lfs.ousl_lfs_backend.user.model.User;
import com.ousl.lfs.ousl_lfs_backend.user.model.VerificationToken;
import com.ousl.lfs.ousl_lfs_backend.user.repo.UserRepository;
import com.ousl.lfs.ousl_lfs_backend.user.repo.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository users;
    private final VerificationTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final UniversityDomainChecker domainChecker;
    private final PasswordPolicy passwordPolicy;
    private final MailService mail;

    @Value("${ulfs.auth.tokenTtlMinutes:60}")
    long ttlMinutes;

    @Transactional
    public void register(RegisterRequest req) {
        domainChecker.assertAllowed(req.email());

        if (users.existsByEmail(req.email().toLowerCase()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account with this email already exists");
        if (users.existsByUniversityId(req.universityId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account with this University ID already exists");

        passwordPolicy.validate(req.password());

        User user = new User();
        user.setFullName(req.fullName());
        user.setEmail(req.email().toLowerCase());
        user.setUniversityId(req.universityId());
        user.setRole(req.role().toUpperCase()); // "STUDENT" / "STAFF"
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setEnabled(false);
        users.save(user);

        VerificationToken vt = new VerificationToken();
        vt.setToken(UUID.randomUUID().toString());
        vt.setUser(user);
        vt.setExpiresAt(Instant.now().plus(ttlMinutes, ChronoUnit.MINUTES));
        tokens.save(vt);

        mail.sendVerificationEmail(user.getEmail(), vt.getToken());
    }

    @Transactional
    public void verify(String rawToken) {
        VerificationToken vt = tokens.findByToken(rawToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));
        if (vt.isUsed() || vt.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired or already used");
        }
        User u = vt.getUser();
        u.setEnabled(true);
        vt.setUsed(true);
        // JPA will flush changes automatically
    }
}
