package com.ousl.lfs.ousl_lfs_backend.auth.service;

import com.ousl.lfs.ousl_lfs_backend.auth.model.RevokedToken;
import com.ousl.lfs.ousl_lfs_backend.auth.repo.RevokedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final JwtService jwt;
    private final RevokedTokenRepository revoked;

    public void logout(String rawToken) {
        String jti = jwt.extractJti(rawToken);

        RevokedToken rt = new RevokedToken();
        rt.setJti(jti);
        rt.setExpiresAt(jwt.extractExpiry(rawToken));

        revoked.save(rt);
    }
}
