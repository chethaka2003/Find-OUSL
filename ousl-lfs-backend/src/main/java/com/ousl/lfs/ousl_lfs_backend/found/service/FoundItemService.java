package com.ousl.lfs.ousl_lfs_backend.found.service;

import com.ousl.lfs.ousl_lfs_backend.found.dto.FoundItemLocationUpdateRequest;
import com.ousl.lfs.ousl_lfs_backend.found.model.FoundItemReport;
import com.ousl.lfs.ousl_lfs_backend.found.repo.FoundItemReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FoundItemService {

    private final FoundItemReportRepository repo;

    public FoundItemReport updateLocation(Long id, FoundItemLocationUpdateRequest req) {
        FoundItemReport r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Found item not found"));

        r.setPossessionStatus(req.possessionStatus());
        r.setHoldingLocationDetails(req.holdingLocationDetails());
        r.setAvailabilityTimes(req.availabilityTimes());
        r.setPreferredContactMethod(req.preferredContactMethod());

        return repo.save(r);
    }
}
