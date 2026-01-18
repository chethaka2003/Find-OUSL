package com.ousl.lfs.ousl_lfs_backend.found.controller;

import com.ousl.lfs.ousl_lfs_backend.found.dto.FoundItemResponse;
import com.ousl.lfs.ousl_lfs_backend.found.service.FoundItemService;
import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/found-items")
@RequiredArgsConstructor
public class FoundItemController {

    private final FoundItemService foundItemService;

    // FR8: Create Found Item report (JWT required)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FoundItemResponse> create(
            @RequestParam @NotNull ItemCategory category,
            @RequestParam @NotBlank String description,
            @RequestParam @NotBlank String foundLocation,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime foundAt,
            @RequestParam(required = false) String serialNumberOrMarkings,
            @RequestPart(required = false) List<MultipartFile> photos
    ) {
        FoundItemResponse saved = foundItemService.createFoundReport(
                category,
                description,
                foundLocation,
                foundAt.toInstant(),
                serialNumberOrMarkings,
                photos
        );
        return ResponseEntity.ok(saved);
    }
}
