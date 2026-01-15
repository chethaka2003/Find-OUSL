package com.ousl.lfs.ousl_lfs_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank String newPassword
) {}
