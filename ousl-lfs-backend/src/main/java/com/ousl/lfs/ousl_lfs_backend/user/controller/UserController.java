package com.ousl.lfs.ousl_lfs_backend.user.controller;

import com.ousl.lfs.ousl_lfs_backend.user.model.User;
import com.ousl.lfs.ousl_lfs_backend.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {
        // auth.getName() should be the email (JWT subject)
        String email = auth.getName();

        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        return ResponseEntity.ok(Map.of(
                "email", u.getEmail(),
                "fullName", u.getFullName(),
                "role", String.valueOf(u.getRole())
        ));
    }
}
