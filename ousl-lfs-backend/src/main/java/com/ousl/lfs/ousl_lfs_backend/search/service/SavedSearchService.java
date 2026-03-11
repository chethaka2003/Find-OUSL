package com.ousl.lfs.ousl_lfs_backend.search.service;

import com.ousl.lfs.ousl_lfs_backend.search.dto.SaveLostSearchRequest;
import com.ousl.lfs.ousl_lfs_backend.search.dto.SavedSearchResponse;
import com.ousl.lfs.ousl_lfs_backend.search.model.SavedSearch;
import com.ousl.lfs.ousl_lfs_backend.search.repo.SavedSearchRepository;
import com.ousl.lfs.ousl_lfs_backend.user.model.User;
import com.ousl.lfs.ousl_lfs_backend.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedSearchService {

    private final SavedSearchRepository repo;
    private final UserRepository userRepository;

    private User currentUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    @Transactional
    public SavedSearchResponse saveLostSearch(Authentication auth, SaveLostSearchRequest req) {
        User u = currentUser(auth);

        Instant from = req.dateFrom() == null ? null : req.dateFrom().toInstant();
        Instant to = req.dateTo() == null ? null : req.dateTo().toInstant();

        // validate date range
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("dateFrom cannot be after dateTo");
        }

        SavedSearch s = SavedSearch.builder()
                .user(u)
                .type("LOST_ITEMS")
                .name(req.name())
                .category(req.category())
                .keyword(req.keyword())
                .location(req.location())
                .dateFrom(from)
                .dateTo(to)
                .build();

        SavedSearch saved = repo.save(s);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<SavedSearchResponse> listLostSearches(Authentication auth) {
        User u = currentUser(auth);
        return repo.findByUserIdAndTypeOrderByCreatedAtDesc(u.getId(), "LOST_ITEMS")
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void delete(Authentication auth, Long id) {
        User u = currentUser(auth);
        SavedSearch s = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Saved search not found"));

        if (!s.getUser().getId().equals(u.getId())) {
            throw new SecurityException("Not allowed");
        }
        repo.delete(s);
    }

    private SavedSearchResponse toDto(SavedSearch s) {
        return new SavedSearchResponse(
                s.getId(),
                s.getType(),
                s.getName(),
                s.getCategory(),
                s.getKeyword(),
                s.getLocation(),
                s.getDateFrom(),
                s.getDateTo(),
                s.getCreatedAt()
        );
    }
}
