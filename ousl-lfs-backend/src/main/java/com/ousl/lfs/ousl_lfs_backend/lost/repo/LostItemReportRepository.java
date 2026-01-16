package com.ousl.lfs.ousl_lfs_backend.lost.repo;

import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostItemReportRepository extends JpaRepository<LostItemReport, Long> {
    boolean existsByTrackingNumber(String trackingNumber);
}
