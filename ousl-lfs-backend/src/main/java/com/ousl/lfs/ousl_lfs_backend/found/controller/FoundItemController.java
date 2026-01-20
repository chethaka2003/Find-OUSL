package com.ousl.lfs.ousl_lfs_backend.found.controller;

import com.ousl.lfs.ousl_lfs_backend.found.dto.FoundItemLocationUpdateRequest;
import com.ousl.lfs.ousl_lfs_backend.found.service.FoundItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/found-items")
@RequiredArgsConstructor
public class FoundItemController {

    private final FoundItemService service;

    @PatchMapping("/{id}/location")
    public Object updateLocation(
            @PathVariable Long id,
            @RequestBody FoundItemLocationUpdateRequest req
    ) {
        return service.updateLocation(id, req);
    }
}
