package com.ousl.lfs.ousl_lfs_backend.lost.controller;

import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemListItemResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemPageResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import com.ousl.lfs.ousl_lfs_backend.lost.service.LostItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/lost-items")
@RequiredArgsConstructor
public class LostItemSearchController {

    private final LostItemService lostItemService;

    /**
     * FR7: GET /api/lost-items?category=ELECTRONICS&q=phone&from=...&to=...&page=0&size=10&sort=lostAt,desc
     */
    @GetMapping
    public ResponseEntity<LostItemPageResponse<LostItemListItemResponse>> search(
            @RequestParam(required = false) ItemCategory category,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) OffsetDateTime from,
            @RequestParam(required = false) OffsetDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lostAt,desc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        Page<LostItemListItemResponse> result =
                lostItemService.searchLostItems(category, q, from, to, pageable);

        return ResponseEntity.ok(new LostItemPageResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        ));
    }

    private Sort parseSort(String sort) {
        // format: "lostAt,desc" OR "createdAt,asc"
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "lostAt");
        }

        String[] parts = sort.split(",", 2);
        String field = parts[0].trim();

        Sort.Direction dir = Sort.Direction.DESC;
        if (parts.length == 2 && "asc".equalsIgnoreCase(parts[1].trim())) {
            dir = Sort.Direction.ASC;
        }

        return Sort.by(dir, field);
    }
}
