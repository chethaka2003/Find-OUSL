package com.ousl.lfs.ousl_lfs_backend.lost.repo;

import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostItemAuditLogRepository extends JpaRepository<LostItemAuditLog, Long> {
}
