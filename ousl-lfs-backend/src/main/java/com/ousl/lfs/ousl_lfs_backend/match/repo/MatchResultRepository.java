package com.ousl.lfs.ousl_lfs_backend.match.repo;

import com.ousl.lfs.ousl_lfs_backend.match.model.MatchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {

    boolean existsByLostReport_IdAndFoundReport_Id(Long lostId, Long foundId);

    // keep your existing method (safe)
    List<MatchResult> findByLostReport_Id(Long lostId);

    // ✅ Needed by MatchController paging
    Page<MatchResult> findByLostReport_IdOrderByMatchScoreDesc(Long lostId, Pageable pageable);

    // ✅ Needed for /found/{foundId}
    Page<MatchResult> findByFoundReport_IdOrderByMatchScoreDesc(Long foundId, Pageable pageable);
}
