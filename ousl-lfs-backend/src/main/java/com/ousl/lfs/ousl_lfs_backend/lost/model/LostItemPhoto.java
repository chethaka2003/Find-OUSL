package com.ousl.lfs.ousl_lfs_backend.lost.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "lost_item_photos", indexes = {
        @Index(name = "idx_lost_photo_report", columnList = "report_id")
})
@Getter
@Setter
public class LostItemPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private LostItemReport report;

    @Column(nullable = false, length = 500)
    private String filePath; // saved path in server

    @Column(nullable = false, length = 255)
    private String originalName;

    @CreationTimestamp
    private Instant createdAt;



}
