package com.ousl.lfs.ousl_lfs_backend.found.dto;

import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;

import java.time.Instant;
import java.util.List;

public record FoundItemResponse(
        Long id,
        String trackingNumber,
        ItemCategory category,
        String description,
        String foundLocation,
        Instant foundAt,
        String serialNumberOrMarkings,
        List<String> photoPaths
) {}
