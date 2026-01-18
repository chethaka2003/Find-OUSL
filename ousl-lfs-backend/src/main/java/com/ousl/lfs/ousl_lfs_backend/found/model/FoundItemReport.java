package com.ousl.lfs.ousl_lfs_backend.found.model;

import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import com.ousl.lfs.ousl_lfs_backend.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "found_item_reports",
        indexes = {
                @Index(name = "idx_found_tracking", columnList = "trackingNumber", unique = true),
                @Index(name = "idx_found_category", columnList = "category"),
                @Index(name = "idx_found_foundAt", columnList = "foundAt")
        }
)
@Getter
@Setter
public class FoundItemReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true, length = 64)
    private String trackingNumber; // set after first save (uses id)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ItemCategory category;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, length = 255)
    private String foundLocation;

    @Column(nullable = false)
    private Instant foundAt;

    @Column(nullable = true, length = 255)
    private String serialNumberOrMarkings;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_found_report_user"))
    private User user;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoundItemPhoto> photos = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;

    public void addPhoto(FoundItemPhoto p) {
        photos.add(p);
        p.setReport(this);
    }
}
