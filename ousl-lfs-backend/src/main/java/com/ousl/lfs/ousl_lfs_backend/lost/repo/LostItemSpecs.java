package com.ousl.lfs.ousl_lfs_backend.lost.repo;

import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public final class LostItemSpecs {

    private LostItemSpecs() {}

    public static Specification<LostItemReport> categoryIs(ItemCategory category) {
        return (root, query, cb) -> {
            if (category == null) return cb.conjunction();
            return cb.equal(root.get("category"), category);
        };
    }

    public static Specification<LostItemReport> keywordLike(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();
            String like = "%" + q.trim().toLowerCase() + "%";

            // search in description, lostLocation, trackingNumber (you can add more)
            return cb.or(
                    cb.like(cb.lower(root.get("description")), like),
                    cb.like(cb.lower(root.get("lostLocation")), like),
                    cb.like(cb.lower(root.get("trackingNumber")), like)
            );
        };
    }

    public static Specification<LostItemReport> lostAtFrom(OffsetDateTime from) {
        return (root, query, cb) -> {
            if (from == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("lostAt"), from);
        };
    }

    public static Specification<LostItemReport> lostAtTo(OffsetDateTime to) {
        return (root, query, cb) -> {
            if (to == null) return cb.conjunction();
            return cb.lessThanOrEqualTo(root.get("lostAt"), to);
        };
    }
}
