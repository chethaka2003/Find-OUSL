package com.ousl.lfs.ousl_lfs_backend.match.service;

import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import com.ousl.lfs.ousl_lfs_backend.lost.repo.LostItemReportRepository;

// 👇 CHANGE these imports if your found entity/repo package is different
import com.ousl.lfs.ousl_lfs_backend.found.model.FoundItemReport;
import com.ousl.lfs.ousl_lfs_backend.found.repo.FoundItemReportRepository;

import com.ousl.lfs.ousl_lfs_backend.match.dto.MatchItemSummary;
import com.ousl.lfs.ousl_lfs_backend.match.dto.MatchResultResponse;
import com.ousl.lfs.ousl_lfs_backend.match.util.MatchScoring;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final LostItemReportRepository lostRepo;
    private final FoundItemReportRepository foundRepo;

    // ---------- Matches for a LOST report (suggest FOUND items) ----------
    @Transactional(readOnly = true)
    public List<MatchResultResponse> matchFoundForLost(Long lostId, Pageable pageable) {
        LostItemReport lost = lostRepo.findById(lostId)
                .orElseThrow(() -> new IllegalArgumentException("Lost item not found: " + lostId));

        // You can optimize later with specs, but this is stable and won't break FRs.
        List<FoundItemReport> candidates = foundRepo.findAll(pageable).getContent();

        return candidates.stream()
                .map(f -> buildMatchForLost(lost, f))
                .filter(r -> r.score() >= 25) // minimum threshold
                .sorted(Comparator.comparingInt(MatchResultResponse::score).reversed())
                .collect(Collectors.toList());
    }

    // ---------- Matches for a FOUND report (suggest LOST items) ----------
    @Transactional(readOnly = true)
    public List<MatchResultResponse> matchLostForFound(Long foundId, Pageable pageable) {
        FoundItemReport found = foundRepo.findById(foundId)
                .orElseThrow(() -> new IllegalArgumentException("Found item not found: " + foundId));

        List<LostItemReport> candidates = lostRepo.findAll(pageable).getContent();

        return candidates.stream()
                .map(l -> buildMatchForFound(found, l))
                .filter(r -> r.score() >= 25)
                .sorted(Comparator.comparingInt(MatchResultResponse::score).reversed())
                .collect(Collectors.toList());
    }

    private MatchResultResponse buildMatchForLost(LostItemReport lost, FoundItemReport found) {
        int score = 0;
        StringBuilder reason = new StringBuilder();

        // 1) Serial/IMEI/markings
        int serialScore = MatchScoring.scoreTextStrong(
                lost.getSerialNumberOrMarkings(),
                found.getSerialNumberOrMarkings()
        );
        if (serialScore > 0) { score += serialScore; reason.append("Serial/IMEI match; "); }

        // 2) Category
        int catScore = MatchScoring.scoreCategory(
                safeStr(lost.getCategory()),
                safeStr(found.getCategory())
        );
        if (catScore > 0) { score += catScore; reason.append("Category match; "); }

        // 3) Location
        int locScore = MatchScoring.scoreLocation(lost.getLostLocation(), found.getFoundLocation());
        if (locScore > 0) { score += locScore; reason.append("Location similar; "); }

        // 4) Date closeness
        int dateScore = MatchScoring.scoreDateClose(lost.getLostAt(), found.getFoundAt());
        if (dateScore > 0) { score += dateScore; reason.append("Date close; "); }

        MatchItemSummary summary = new MatchItemSummary(
                found.getId(),
                found.getTrackingNumber(),
                safeStr(found.getCategory()),
                found.getDescription(),
                found.getFoundLocation(),
                found.getFoundAt(),
                found.getSerialNumberOrMarkings(),
                safePhotoPaths(found)
        );

        return new MatchResultResponse(score, reason.toString().trim(), summary);
    }

    private MatchResultResponse buildMatchForFound(FoundItemReport found, LostItemReport lost) {
        int score = 0;
        StringBuilder reason = new StringBuilder();

        int serialScore = MatchScoring.scoreTextStrong(
                found.getSerialNumberOrMarkings(),
                lost.getSerialNumberOrMarkings()
        );
        if (serialScore > 0) { score += serialScore; reason.append("Serial/IMEI match; "); }

        int catScore = MatchScoring.scoreCategory(
                safeStr(found.getCategory()),
                safeStr(lost.getCategory())
        );
        if (catScore > 0) { score += catScore; reason.append("Category match; "); }

        int locScore = MatchScoring.scoreLocation(found.getFoundLocation(), lost.getLostLocation());
        if (locScore > 0) { score += locScore; reason.append("Location similar; "); }

        int dateScore = MatchScoring.scoreDateClose(found.getFoundAt(), lost.getLostAt());
        if (dateScore > 0) { score += dateScore; reason.append("Date close; "); }

        MatchItemSummary summary = new MatchItemSummary(
                lost.getId(),
                lost.getTrackingNumber(),
                safeStr(lost.getCategory()),
                lost.getDescription(),
                lost.getLostLocation(),
                lost.getLostAt(),
                lost.getSerialNumberOrMarkings(),
                safePhotoPaths(lost)
        );

        return new MatchResultResponse(score, reason.toString().trim(), summary);
    }

    private String safeStr(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    // ---- These two methods avoid breaking your existing FR photo model ----
    // If you store photos as List<String> in entity => return it directly.
    // If you store as List<PhotoEntity> => map to filePath field.
    @SuppressWarnings("unchecked")
    private List<String> safePhotoPaths(Object report) {
        try {
            // LostItemReport / FoundItemReport may have getPhotoPaths()
            var m = report.getClass().getMethod("getPhotoPaths");
            Object v = m.invoke(report);
            if (v instanceof List<?> list) return (List<String>) list;
        } catch (Exception ignored) {}

        try {
            // Or may have getPhotos() returning entities with getFilePath()
            var m = report.getClass().getMethod("getPhotos");
            Object v = m.invoke(report);
            if (v instanceof List<?> list) {
                return list.stream()
                        .map(p -> {
                            try {
                                var gm = p.getClass().getMethod("getFilePath");
                                Object fp = gm.invoke(p);
                                return fp == null ? null : fp.toString();
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(x -> x != null && !x.isBlank())
                        .toList();
            }
        } catch (Exception ignored) {}

        return List.of();
    }
}
