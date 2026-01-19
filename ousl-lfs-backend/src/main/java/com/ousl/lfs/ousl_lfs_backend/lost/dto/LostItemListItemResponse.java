package com.ousl.lfs.ousl_lfs_backend.lost.dto;

import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;

import java.time.Instant;
import java.util.List;

public record LostItemListItemResponse(
        Long id,
        String trackingNumber,
        ItemCategory category,
        String description,
        String lostLocation,
        Instant lostAt,
        List<String> photoPaths
) {}
