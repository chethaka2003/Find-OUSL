package com.ousl.lfs.ousl_lfs_backend.found.dto;

import com.ousl.lfs.ousl_lfs_backend.found.model.*;

public record FoundItemLocationUpdateRequest(
        PossessionStatus possessionStatus,
        String holdingLocationDetails,
        String availabilityTimes,
        PreferredContactMethod preferredContactMethod
) {}
