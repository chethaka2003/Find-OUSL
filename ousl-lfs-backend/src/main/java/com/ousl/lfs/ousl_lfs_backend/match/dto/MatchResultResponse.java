package com.ousl.lfs.ousl_lfs_backend.match.dto;

public record MatchResultResponse(
        int score,
        String reason,
        MatchItemSummary item
) {}
