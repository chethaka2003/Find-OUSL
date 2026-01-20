package com.ousl.lfs.ousl_lfs_backend.search.controller;

import com.ousl.lfs.ousl_lfs_backend.search.dto.SaveLostSearchRequest;
import com.ousl.lfs.ousl_lfs_backend.search.dto.SavedSearchResponse;
import com.ousl.lfs.ousl_lfs_backend.search.service.SavedSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saved-searches")
@RequiredArgsConstructor
public class SavedSearchController {

    private static final String MSG = "message";
    private final SavedSearchService service;

    // FR10.2 Save search criteria (lost items)
    @PostMapping("/lost-items")
    public ResponseEntity<SavedSearchResponse> saveLost(Authentication auth,
                                                        @Valid @RequestBody SaveLostSearchRequest req) {
        return ResponseEntity.ok(service.saveLostSearch(auth, req));
    }

    // View saved searches
    @GetMapping("/lost-items")
    public ResponseEntity<List<SavedSearchResponse>> listLost(Authentication auth) {
        return ResponseEntity.ok(service.listLostSearches(auth));
    }

    // Delete saved search
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(Authentication auth, @PathVariable Long id) {
        service.delete(auth, id);
        return ResponseEntity.ok(Map.of(MSG, "Deleted"));
    }
}
