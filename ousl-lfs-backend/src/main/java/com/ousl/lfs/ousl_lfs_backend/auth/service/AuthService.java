package com.ousl.lfs.ousl_lfs_backend.auth.service;

import com.ousl.lfs.ousl_lfs_backend.auth.dto.LoginRequest;
import com.ousl.lfs.ousl_lfs_backend.auth.dto.LoginResponse;
import com.ousl.lfs.ousl_lfs_backend.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserRepository users;
    private final JwtService jwt;

    private static final int MAX_ATTEMPTS = 5;

    public LoginResponse login(LoginRequest req) {
        String email = req.email().toLowerCase();

        // For remaining attempts + lock behavior we need the user record (if exists)
        var userOpt = users.findByEmail(email);

        // If user exists, check locked first
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
                throw new ResponseStatusException(HttpStatus.LOCKED,
                        "Account locked. Try again later or reset password.");
            }
        }

        try {
            var token = new UsernamePasswordAuthenticationToken(email, req.password());
            var auth = authManager.authenticate(token);

            var principal = (UserPrincipal) auth.getPrincipal();
            var user = principal.getUser();

            if (!user.isEnabled()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please verify your email before login.");
            }

            // success → reset attempts
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            users.save(user);

            String jwtToken = jwt.generateToken(user.getEmail(), user.getRole());
            return new LoginResponse(jwtToken, user.getRole(), user.getFullName());

        } catch (BadCredentialsException ex) {
            // wrong password → increase attempts if user exists
            if (userOpt.isPresent()) {
                var user = userOpt.get();

                int attempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(attempts);

                if (attempts >= MAX_ATTEMPTS) {
                    user.setLockedUntil(Instant.now().plus(30, ChronoUnit.MINUTES)); // lock period
                    users.save(user);
                    throw new ResponseStatusException(HttpStatus.LOCKED,
                            "Account locked after 5 failed attempts. Use password reset.");
                } else {
                    users.save(user);
                    int remaining = MAX_ATTEMPTS - attempts;
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                            "Invalid credentials. Remaining attempts: " + remaining);
                }
            }

            // If email not found, still return safe message (don’t reveal account existence)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        }
    }
}
