package com.ousl.lfs.ousl_lfs_backend.lost.controller;

import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemDashboardItemResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemPageResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.service.LostItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lost-items/my")
@RequiredArgsConstructor
public class LostItemMyController {

    private final LostItemService lostItemService;

    @GetMapping
    public LostItemPageResponse<LostItemDashboardItemResponse> myReports(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return lostItemService.myReports(auth.getName(), pageable);
    }

    @PatchMapping("/{id}/cancel")
    public void cancel(Authentication auth, @PathVariable Long id) {
        lostItemService.cancelMyReport(auth.getName(), id);
    }
}
