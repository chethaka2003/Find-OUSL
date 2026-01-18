package com.ousl.lfs.ousl_lfs_backend.found.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "found_item_photos",
        indexes = @Index(name = "idx_found_photo_report", columnList = "report_id"))
@Getter
@Setter
public class FoundItemPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Use "path" getter name to avoid your earlier getPath() issues
    @Column(nullable = false, length = 500)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_found_photo_report"))
    private FoundItemReport report;
}
