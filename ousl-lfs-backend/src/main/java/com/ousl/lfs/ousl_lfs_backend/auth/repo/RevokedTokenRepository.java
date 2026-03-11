package com.ousl.lfs.ousl_lfs_backend.auth.repo;

import com.ousl.lfs.ousl_lfs_backend.auth.model.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {
    boolean existsByJti(String jti);
}
