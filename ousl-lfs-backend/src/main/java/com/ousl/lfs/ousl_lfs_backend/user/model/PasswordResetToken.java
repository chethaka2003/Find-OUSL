package com.ousl.lfs.ousl_lfs_backend.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens",
        indexes = @Index(name = "idx_reset_token", columnList = "token"))
@Getter @Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private int failedAttempts = 0;

    @CreationTimestamp
    private Instant createdAt;
}
