package com.ousl.lfs.ousl_lfs_backend.lost.dto;

import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostReportStatus;

import java.time.Instant;
import java.util.List;

public record LostItemDashboardItemResponse(
        Long id,
        String trackingNumber,
        ItemCategory category,
        String description,
        String lostLocation,
        Instant lostAt,
        LostReportStatus status,
        Instant createdAt,
        List<String> photoPaths
) {}
