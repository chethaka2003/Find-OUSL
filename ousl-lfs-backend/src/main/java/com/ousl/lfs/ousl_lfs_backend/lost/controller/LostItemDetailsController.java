package com.ousl.lfs.ousl_lfs_backend.lost.controller;

import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemUpdateResponse;
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
public class LostItemDetailsController {

    private final LostItemService lostItemService;

    @PatchMapping(value = "/{id}/details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LostItemUpdateResponse> updateDetails(
            @PathVariable("id") Long id,
            @RequestParam(value = "serialNumberOrMarkings", required = false) String serialNumberOrMarkings,
            @RequestParam(value = "distinguishingFeatures", required = false) String distinguishingFeatures,
            @RequestParam(value = "estimatedValue", required = false) Double estimatedValue,
            @RequestParam(value = "rewardAmount", required = false) Double rewardAmount,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            Authentication auth
    ) {
        String email = auth.getName();

        LostItemUpdateResponse res = lostItemService.updateLostItemDetails(
                id, email,
                serialNumberOrMarkings, distinguishingFeatures,
                estimatedValue, rewardAmount,
                photos
        );

        return ResponseEntity.ok(res);
    }
}
