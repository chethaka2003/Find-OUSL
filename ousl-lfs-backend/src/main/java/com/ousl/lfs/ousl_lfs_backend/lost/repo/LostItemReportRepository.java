package com.ousl.lfs.ousl_lfs_backend.lost.repo;

import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface LostItemReportRepository extends JpaRepository<LostItemReport, Long>, JpaSpecificationExecutor<LostItemReport> {

    boolean existsByTrackingNumber(String trackingNumber);

    @EntityGraph(attributePaths = {"user", "photos"})
    Optional<LostItemReport> findWithUserById(Long id);

    // ✅ FR7: dashboard list (my reports)
    @EntityGraph(attributePaths = {"photos"})
    Page<LostItemReport> findByUser_Email(String email, Pageable pageable);

    List<LostItemReport> findByCategory(ItemCategory category);
}
