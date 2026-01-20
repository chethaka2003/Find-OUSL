package com.ousl.lfs.ousl_lfs_backend.match.model;

import com.ousl.lfs.ousl_lfs_backend.found.model.FoundItemReport;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "match_results",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"lost_report_id", "found_report_id"}
        ))
@Getter
@Setter
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lost_report_id")
    private LostItemReport lostReport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "found_report_id")
    private FoundItemReport foundReport;

    @Column(nullable = false)
    private int matchScore; // 0–100

    @Column(nullable = false)
    private boolean notified = false;

    @CreationTimestamp
    private Instant createdAt;
}
