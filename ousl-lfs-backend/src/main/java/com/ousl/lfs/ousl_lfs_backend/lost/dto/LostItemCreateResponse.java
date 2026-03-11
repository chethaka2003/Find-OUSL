package com.ousl.lfs.ousl_lfs_backend.lost.dto;

import java.util.List;

public record LostItemCreateResponse(
        Long id,
        String trackingNumber,
        String category,
        String description,
        String lostLocation,
        String lostAt,
        List<String> photoPaths
) {}
