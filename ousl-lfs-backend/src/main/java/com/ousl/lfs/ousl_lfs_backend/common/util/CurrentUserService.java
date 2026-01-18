package com.ousl.lfs.ousl_lfs_backend.common.util;

import com.ousl.lfs.ousl_lfs_backend.user.model.User;
import com.ousl.lfs.ousl_lfs_backend.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User requireUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("No authenticated user");
        }

        String email = auth.getName(); // your JWT sets subject = email (from your earlier FRs)
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found: " + email));
    }
}
