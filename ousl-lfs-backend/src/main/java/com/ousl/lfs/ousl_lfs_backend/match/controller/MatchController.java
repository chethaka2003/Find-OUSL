package com.ousl.lfs.ousl_lfs_backend.match.controller;

import com.ousl.lfs.ousl_lfs_backend.match.model.MatchResult;
import com.ousl.lfs.ousl_lfs_backend.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/lost/{lostId}")
    public ResponseEntity<Page<MatchResult>> matchFoundForLost(
            @PathVariable Long lostId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                matchService.matchFoundForLost(lostId, pageable)
        );
    }

    @GetMapping("/found/{foundId}")
    public ResponseEntity<Page<MatchResult>> matchLostForFound(
            @PathVariable Long foundId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                matchService.matchLostForFound(foundId, pageable)
        );
    }
}
