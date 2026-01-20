package com.ousl.lfs.ousl_lfs_backend.search.dto;

import java.time.Instant;

public record SavedSearchResponse(
        Long id,
        String type,
        String name,
        String category,
        String keyword,
        String location,
        Instant dateFrom,
        Instant dateTo,
        Instant createdAt
) {}
