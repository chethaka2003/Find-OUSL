package com.ousl.lfs.ousl_lfs_backend.lost.repo;

import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LostItemReportRepository extends JpaRepository<LostItemReport, Long>, JpaSpecificationExecutor<LostItemReport> {

    boolean existsByTrackingNumber(String trackingNumber);

    @EntityGraph(attributePaths = {"user", "photos"})
    Optional<LostItemReport> findWithUserById(Long id);
}
