package com.ousl.lfs.ousl_lfs_backend.search.repo;

import com.ousl.lfs.ousl_lfs_backend.search.model.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {
    List<SavedSearch> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);
}
