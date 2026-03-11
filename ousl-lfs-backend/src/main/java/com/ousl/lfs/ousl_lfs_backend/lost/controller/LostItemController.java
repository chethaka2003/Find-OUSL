package com.ousl.lfs.ousl_lfs_backend.lost.controller;

import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemCreateResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.service.LostItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/lost-items")
@RequiredArgsConstructor
public class LostItemController {

    private final LostItemService lostItemService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LostItemCreateResponse> createLostItemReport(
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("lostLocation") String lostLocation,
            @RequestParam("lostAt") String lostAtIso, // example: 2026-01-16T10:30:00+05:30
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            Authentication auth
    ) {
        // Spring Security "username" should be the email (your JWT subject)
        String email = auth.getName();

        LostItemCreateResponse res = lostItemService.createReport(
                email, category, description, lostLocation, lostAtIso, photos
        );
        return ResponseEntity.ok(res);
    }
}
