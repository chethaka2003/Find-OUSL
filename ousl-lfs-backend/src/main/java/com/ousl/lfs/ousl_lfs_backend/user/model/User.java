package com.ousl.lfs.ousl_lfs_backend.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name="users", uniqueConstraints = {
        @UniqueConstraint(name="uk_users_email", columnNames="email"),
        @UniqueConstraint(name="uk_users_university_id", columnNames="universityId")
})
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String fullName;
    @Column(nullable=false) private String email;
    @Column(nullable=false) private String universityId;
    @Column(nullable=false) private String passwordHash;
    @Column(nullable=false) private String role;
    @Column(nullable=false) private boolean enabled = false;

    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    private Instant lockedUntil;

    @CreationTimestamp
    private Instant createdAt;
}
