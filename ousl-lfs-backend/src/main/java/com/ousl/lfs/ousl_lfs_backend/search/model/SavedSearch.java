package com.ousl.lfs.ousl_lfs_backend.search.model;

import com.ousl.lfs.ousl_lfs_backend.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "saved_searches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who saved this search
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Search type: LOST_ITEMS (future: FOUND_ITEMS)
    @Column(nullable = false, length = 40)
    private String type;

    // Optional user-friendly name
    @Column(nullable = false, length = 100)
    private String name;

    // Stored filter values
    @Column(length = 50)
    private String category;

    @Column(length = 200)
    private String keyword;

    @Column(length = 200)
    private String location;

    private Instant dateFrom;
    private Instant dateTo;

    // created at
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
