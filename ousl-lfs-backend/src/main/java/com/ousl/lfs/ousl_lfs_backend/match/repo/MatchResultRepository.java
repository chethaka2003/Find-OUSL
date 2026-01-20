package com.ousl.lfs.ousl_lfs_backend.match.repo;

import com.ousl.lfs.ousl_lfs_backend.match.model.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {

    boolean existsByLostReport_IdAndFoundReport_Id(Long lostId, Long foundId);

    List<MatchResult> findByLostReport_Id(Long lostId);
}
