package com.ousl.lfs.ousl_lfs_backend.lost.dto;

import java.util.List;

public record LostItemPageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}
