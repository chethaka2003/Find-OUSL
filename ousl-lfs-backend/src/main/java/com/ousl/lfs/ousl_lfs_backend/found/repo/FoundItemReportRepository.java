package com.ousl.lfs.ousl_lfs_backend.found.repo;

import com.ousl.lfs.ousl_lfs_backend.found.model.FoundItemReport;
import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoundItemReportRepository extends JpaRepository<FoundItemReport, Long> {
    boolean existsByTrackingNumber(String trackingNumber);

    List<FoundItemReport> findByCategory(ItemCategory category);
}
