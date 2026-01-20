package com.ousl.lfs.ousl_lfs_backend.match.service;

import com.ousl.lfs.ousl_lfs_backend.found.model.FoundItemReport;
import com.ousl.lfs.ousl_lfs_backend.found.repo.FoundItemReportRepository;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostReportStatus;
import com.ousl.lfs.ousl_lfs_backend.match.model.MatchResult;
import com.ousl.lfs.ousl_lfs_backend.match.repo.MatchResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final FoundItemReportRepository foundRepo;
    private final MatchResultRepository matchRepo;
    private final MatchEngine engine;

    private static final int MATCH_THRESHOLD = 60;

    @Transactional
    public void matchForLostItem(LostItemReport lost) {

        List<FoundItemReport> foundItems =
                foundRepo.findByCategory(lost.getCategory());

        for (FoundItemReport found : foundItems) {

            if (matchRepo.existsByLostReport_IdAndFoundReport_Id(
                    lost.getId(), found.getId())) {
                continue; // avoid duplicates
            }

            int score = engine.calculateScore(lost, found);

            if (score >= MATCH_THRESHOLD) {
                MatchResult result = new MatchResult();
                result.setLostReport(lost);
                result.setFoundReport(found);
                result.setMatchScore(score);

                matchRepo.save(result);

                // Update status (soft)
                lost.setStatus(LostReportStatus.POTENTIAL_MATCH);
            }
        }
    }
}
