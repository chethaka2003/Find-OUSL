package com.ousl.lfs.ousl_lfs_backend.lost.model;

public enum ItemCategory {
    ELECTRONICS,
    DOCUMENTS,
    ACCESSORIES,
    CLOTHING,
    BOOKS,
    OTHER;

    public static ItemCategory from(String raw) {
        if (raw == null) throw new IllegalArgumentException("Category is required");
        return ItemCategory.valueOf(raw.trim().toUpperCase());
    }
}
