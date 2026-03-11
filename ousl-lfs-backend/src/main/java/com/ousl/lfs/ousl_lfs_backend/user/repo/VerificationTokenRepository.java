package com.ousl.lfs.ousl_lfs_backend.user.repo;

import com.ousl.lfs.ousl_lfs_backend.user.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}
