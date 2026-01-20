package com.ousl.lfs.ousl_lfs_backend.lost.repo;

import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public class LostItemSpecs {

    /**
     * Keyword search across trackingNumber, description, lostLocation.
     * (Your entity does NOT have itemName, so we use trackingNumber instead.)
     */
    public static Specification<LostItemReport> keywordLike(String q) {
        if (q == null || q.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String qq = q.toLowerCase();

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("trackingNumber")), "%" + qq + "%"),
                cb.like(cb.lower(root.get("description")), "%" + qq + "%"),
                cb.like(cb.lower(root.get("lostLocation")), "%" + qq + "%")
        );
    }

    /** Explicit location filter (FR10) */
    public static Specification<LostItemReport> locationLike(String location) {
        if (location == null || location.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String loc = location.toLowerCase();
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("lostLocation")), "%" + loc + "%");
    }

    /** Category filter (FR10) */
    public static Specification<LostItemReport> categoryIs(ItemCategory category) {
        if (category == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    /** From date filter (FR10) */
    public static Specification<LostItemReport> lostAtFrom(OffsetDateTime from) {
        if (from == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("lostAt"), from.toInstant());
    }

    /** To date filter (FR10) */
    public static Specification<LostItemReport> lostAtTo(OffsetDateTime to) {
        if (to == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("lostAt"), to.toInstant());
    }
}
