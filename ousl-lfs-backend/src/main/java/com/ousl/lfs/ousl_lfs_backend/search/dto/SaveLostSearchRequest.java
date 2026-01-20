package com.ousl.lfs.ousl_lfs_backend.search.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record SaveLostSearchRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String name,

        String category,
        String keyword,
        String location,

        OffsetDateTime dateFrom,
        OffsetDateTime dateTo
) {}
