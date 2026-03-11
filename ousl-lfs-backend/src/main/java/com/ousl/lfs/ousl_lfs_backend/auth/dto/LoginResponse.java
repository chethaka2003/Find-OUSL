package com.ousl.lfs.ousl_lfs_backend.auth.dto;

public record LoginResponse(
        String token,
        String role,
        String fullName
) {}
