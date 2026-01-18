package com.ousl.lfs.ousl_lfs_backend.found.repo;

import com.ousl.lfs.ousl_lfs_backend.found.model.FoundItemReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoundItemReportRepository extends JpaRepository<FoundItemReport, Long> {
    boolean existsByTrackingNumber(String trackingNumber);
}
