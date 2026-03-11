package com.ousl.lfs.ousl_lfs_backend.match.service;

import com.ousl.lfs.ousl_lfs_backend.found.model.FoundItemReport;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import org.springframework.stereotype.Component;

@Component
public class MatchEngine {

    public int calculateScore(LostItemReport lost, FoundItemReport found) {
        int score = 0;

        // Category match (strong)
        if (lost.getCategory() == found.getCategory()) {
            score += 40;
        }

        // Location similarity
        if (found.getFoundLocation() != null &&
                lost.getLostLocation().toLowerCase()
                        .contains(found.getFoundLocation().toLowerCase())) {
            score += 20;
        }

        // Description keyword overlap
        if (found.getDescription() != null &&
                lost.getDescription().toLowerCase()
                        .contains(found.getDescription().toLowerCase())) {
            score += 20;
        }

        // Serial / markings
        if (lost.getSerialNumberOrMarkings() != null &&
                found.getSerialNumberOrMarkings() != null &&
                lost.getSerialNumberOrMarkings().equalsIgnoreCase(
                        found.getSerialNumberOrMarkings())) {
            score += 20;
        }

        return Math.min(score, 100);
    }
}
