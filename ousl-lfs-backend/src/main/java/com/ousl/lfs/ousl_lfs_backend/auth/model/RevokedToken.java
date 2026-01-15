package com.ousl.lfs.ousl_lfs_backend.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
@Getter @Setter
public class RevokedToken {

    @Id
    @Column(nullable = false, length = 64)
    private String jti;

    @Column(nullable = false)
    private Instant expiresAt;
}
