package com.ousl.lfs.ousl_lfs_backend.lost.controller;

import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemListItemResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import com.ousl.lfs.ousl_lfs_backend.lost.service.LostItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/lost-items")
@RequiredArgsConstructor
public class LostItemSearchController {

    private final LostItemService lostItemService;

    // FR7/FR10: Search lost items
    // Example: /api/lost-items/search?q=phone&category=ELECTRONICS&page=0&size=10&sort=lostAt,desc
    @GetMapping("/search")
    public ResponseEntity<Page<LostItemListItemResponse>> search(
            @RequestParam(required = false) ItemCategory category,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) OffsetDateTime from,
            @RequestParam(required = false) OffsetDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lostAt,desc") String sort
    ) {
        Sort s = parseSort(sort);
        PageRequest pageable = PageRequest.of(page, size, s);

        return ResponseEntity.ok(
                lostItemService.searchLostItems(category, q, from, to, pageable)
        );
    }

    private Sort parseSort(String sort) {
        try {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            String dir = (parts.length > 1) ? parts[1].trim() : "desc";
            return Sort.by("asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC, field);
        } catch (Exception e) {
            return Sort.by(Sort.Direction.DESC, "lostAt");
        }
    }
}
