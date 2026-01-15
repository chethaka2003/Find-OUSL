package com.ousl.lfs.ousl_lfs_backend.user.repo;

import com.ousl.lfs.ousl_lfs_backend.user.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
}
