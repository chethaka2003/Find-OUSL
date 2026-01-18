package com.ousl.lfs.ousl_lfs_backend.match.controller;

import com.ousl.lfs.ousl_lfs_backend.match.dto.MatchResultResponse;
import com.ousl.lfs.ousl_lfs_backend.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // Suggest FOUND items for a given LOST report
    @GetMapping("/lost/{lostId}")
    public ResponseEntity<List<MatchResultResponse>> matchFoundForLost(
            @PathVariable Long lostId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(matchService.matchFoundForLost(lostId, pageable));
    }

    // Suggest LOST reports for a given FOUND item
    @GetMapping("/found/{foundId}")
    public ResponseEntity<List<MatchResultResponse>> matchLostForFound(
            @PathVariable Long foundId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(matchService.matchLostForFound(foundId, pageable));
    }
}
