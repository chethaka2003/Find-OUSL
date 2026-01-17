package com.ousl.lfs.ousl_lfs_backend.lost.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "lost_item_audit_logs", indexes = {
        @Index(name = "idx_lost_audit_report", columnList = "report_id"),
        @Index(name = "idx_lost_audit_user", columnList = "userEmail")
})
@Getter
@Setter
public class LostItemAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private LostItemReport report;

    @Column(nullable = false, length = 64)
    private String action; // e.g., "UPDATE_DETAILS"

    @Column(nullable = false, length = 255)
    private String userEmail;

    @Column(length = 4000)
    private String details; // simple text/json about changes

    @CreationTimestamp
    private Instant createdAt;
}
