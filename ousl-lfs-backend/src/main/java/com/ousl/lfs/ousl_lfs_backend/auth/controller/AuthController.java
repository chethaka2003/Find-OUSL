package com.ousl.lfs.ousl_lfs_backend.auth.controller;

import com.ousl.lfs.ousl_lfs_backend.auth.dto.RegisterRequest;
import com.ousl.lfs.ousl_lfs_backend.auth.service.RegistrationService;
import com.ousl.lfs.ousl_lfs_backend.common.util.SimpleRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ousl.lfs.ousl_lfs_backend.auth.dto.LoginRequest;
import com.ousl.lfs.ousl_lfs_backend.auth.dto.LoginResponse;
import com.ousl.lfs.ousl_lfs_backend.auth.service.AuthService;


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;
    private final SimpleRateLimiter rateLimiter;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest req,
                                                        HttpServletRequest http) {
        String key = "reg:" + clientIp(http) + ":" + req.email().toLowerCase();
        rateLimiter.check(key);
        registrationService.register(req);
        return ResponseEntity.ok(Map.of("message", "Account created. Check your university email to verify."));
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(@RequestParam("token") String token) {
        registrationService.verify(token);
        return ResponseEntity.ok(Map.of("message", "Email verified. You can now sign in."));
    }

    private String clientIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        return (h != null && !h.isBlank()) ? h.split(",")[0].trim() : req.getRemoteAddr();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        rateLimiter.check("login:" + clientIp(http));
        return ResponseEntity.ok(authService.login(req));
    }

}