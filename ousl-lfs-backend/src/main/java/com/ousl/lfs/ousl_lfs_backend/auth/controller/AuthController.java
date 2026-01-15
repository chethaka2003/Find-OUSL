package com.ousl.lfs.ousl_lfs_backend.auth.controller;

import com.ousl.lfs.ousl_lfs_backend.auth.dto.ForgotPasswordRequest;
import com.ousl.lfs.ousl_lfs_backend.auth.dto.LoginRequest;
import com.ousl.lfs.ousl_lfs_backend.auth.dto.LoginResponse;
import com.ousl.lfs.ousl_lfs_backend.auth.dto.RegisterRequest;
import com.ousl.lfs.ousl_lfs_backend.auth.dto.ResetPasswordRequest;
import com.ousl.lfs.ousl_lfs_backend.auth.service.AuthService;
import com.ousl.lfs.ousl_lfs_backend.auth.service.PasswordResetService;
import com.ousl.lfs.ousl_lfs_backend.auth.service.RegistrationService;
import com.ousl.lfs.ousl_lfs_backend.common.util.SimpleRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String MSG = "message";

    private final RegistrationService registrationService;
    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final SimpleRateLimiter rateLimiter;

    // ---------- FR1 ----------
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequest req,
            HttpServletRequest http
    ) {
        rateLimiter.check("register:" + clientIp(http));
        registrationService.register(req);
        return ResponseEntity.ok(Map.of(MSG, "Account created. Check your university email to verify."));
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(@RequestParam("token") String token) {
        registrationService.verify(token);
        return ResponseEntity.ok(Map.of(MSG, "Email verified. You can now sign in."));
    }

    // ---------- FR2 ----------
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest http
    ) {
        rateLimiter.check("login:" + clientIp(http));
        return ResponseEntity.ok(authService.login(req));
    }

    // ---------- FR3 ----------
    @PostMapping("/password/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest req,
            HttpServletRequest http
    ) {
        rateLimiter.check("forgot:" + clientIp(http));
        passwordResetService.requestReset(req);

        // Always same response (security best practice: no email enumeration)
        return ResponseEntity.ok(Map.of(MSG, "If the email exists, a reset link has been sent."));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req
    ) {
        passwordResetService.resetPassword(req);
        return ResponseEntity.ok(Map.of(MSG, "Password updated successfully."));
    }

    private String clientIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        return (h != null && !h.isBlank()) ? h.split(",")[0].trim() : req.getRemoteAddr();
    }
}
