package com.ousl.lfs.ousl_lfs_backend.lost.dto;

import java.util.List;

public record LostItemUpdateResponse(
        Long id,
        String trackingNumber,
        String serialNumberOrMarkings,
        String distinguishingFeatures,
        Double estimatedValue,
        Double rewardAmount,
        List<String> addedPhotoPaths,
        String message
) {}
