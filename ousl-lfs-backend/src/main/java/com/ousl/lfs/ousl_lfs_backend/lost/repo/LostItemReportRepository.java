package com.ousl.lfs.ousl_lfs_backend.lost.repo;

import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LostItemReportRepository extends JpaRepository<LostItemReport, Long> {

    boolean existsByTrackingNumber(String trackingNumber);

    // ✅ FR6 fix: load report + user in one query (avoids LazyInitializationException)
    @EntityGraph(attributePaths = {"user"})
    Optional<LostItemReport> findWithUserById(Long id);
}
