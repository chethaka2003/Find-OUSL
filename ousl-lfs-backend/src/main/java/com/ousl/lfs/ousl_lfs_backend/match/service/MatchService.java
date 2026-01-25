package com.ousl.lfs.ousl_lfs_backend.match.service;

import com.ousl.lfs.ousl_lfs_backend.found.model.FoundItemReport;
import com.ousl.lfs.ousl_lfs_backend.found.repo.FoundItemReportRepository;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostReportStatus;
import com.ousl.lfs.ousl_lfs_backend.lost.repo.LostItemReportRepository;
import com.ousl.lfs.ousl_lfs_backend.match.model.MatchResult;
import com.ousl.lfs.ousl_lfs_backend.match.repo.MatchResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final FoundItemReportRepository foundRepo;
    private final LostItemReportRepository lostRepo;
    private final MatchResultRepository matchRepo;
    private final MatchEngine engine;

    private static final int MATCH_THRESHOLD = 60;

    // =====================================================
    // ✅ REQUIRED BY LostItemService (DO NOT REMOVE)
    // =====================================================
    @Transactional
    public void matchForLostItem(LostItemReport lost) {
        generateMatchesForLost(lost);
    }

    // =====================================================
    // ✅ REQUIRED BY MatchController
    // =====================================================
    @Transactional
    public Page<MatchResult> matchFoundForLost(Long lostId, Pageable pageable) {
        LostItemReport lost = lostRepo.findById(lostId)
                .orElseThrow(() -> new IllegalArgumentException("Lost item not found"));

        generateMatchesForLost(lost);

        return matchRepo.findByLostReport_IdOrderByMatchScoreDesc(lostId, pageable);
    }

    @Transactional
    public Page<MatchResult> matchLostForFound(Long foundId, Pageable pageable) {
        FoundItemReport found = foundRepo.findById(foundId)
                .orElseThrow(() -> new IllegalArgumentException("Found item not found"));

        generateMatchesForFound(found);

        return matchRepo.findByFoundReport_IdOrderByMatchScoreDesc(foundId, pageable);
    }

    // =====================================================
    // INTERNAL MATCH LOGIC (PRIVATE)
    // =====================================================
    private void generateMatchesForLost(LostItemReport lost) {
        List<FoundItemReport> foundItems = foundRepo.findByCategory(lost.getCategory());

        for (FoundItemReport found : foundItems) {
            if (matchRepo.existsByLostReport_IdAndFoundReport_Id(lost.getId(), found.getId())) continue;

            int score = engine.calculateScore(lost, found);
            if (score >= MATCH_THRESHOLD) {
                MatchResult m = new MatchResult();
                m.setLostReport(lost);
                m.setFoundReport(found);
                m.setMatchScore(score);
                matchRepo.save(m);

                lost.setStatus(LostReportStatus.POTENTIAL_MATCH);
            }
        }
    }

    private void generateMatchesForFound(FoundItemReport found) {
        List<LostItemReport> lostItems = lostRepo.findByCategory(found.getCategory());

        for (LostItemReport lost : lostItems) {
            if (matchRepo.existsByLostReport_IdAndFoundReport_Id(lost.getId(), found.getId())) continue;

            int score = engine.calculateScore(lost, found);
            if (score >= MATCH_THRESHOLD) {
                MatchResult m = new MatchResult();
                m.setLostReport(lost);
                m.setFoundReport(found);
                m.setMatchScore(score);
                matchRepo.save(m);

                lost.setStatus(LostReportStatus.POTENTIAL_MATCH);
            }
        }
    }
}
