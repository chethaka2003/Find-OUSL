package com.ousl.lfs.ousl_lfs_backend.lost.model;

import com.ousl.lfs.ousl_lfs_backend.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "lost_item_reports", indexes = {
        @Index(name = "idx_lost_tracking", columnList = "trackingNumber", unique = true),
        @Index(name = "idx_lost_user", columnList = "user_id")
})
@Getter
@Setter
public class LostItemReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ItemCategory category;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 255)
    private String lostLocation;

    @Column(nullable = false)
    private Instant lostAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    private Instant createdAt;

    @Column(length = 255)
    private String serialNumberOrMarkings;

    @Column(length = 2000)
    private String distinguishingFeatures;

    @Column
    private Double estimatedValue;

    @Column
    private Double rewardAmount;

}
