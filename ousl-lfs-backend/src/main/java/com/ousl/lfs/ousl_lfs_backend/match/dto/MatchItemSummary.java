package com.ousl.lfs.ousl_lfs_backend.match.dto;

import java.time.Instant;
import java.util.List;

public record MatchItemSummary(
        Long id,
        String trackingNumber,
        String category,
        String description,
        String location,
        Instant dateTime,                 // ✅ Instant (matches your entities)
        String serialNumberOrMarkings,
        List<String> photoPaths
) {}
