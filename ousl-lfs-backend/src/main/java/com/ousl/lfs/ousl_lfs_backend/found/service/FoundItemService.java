package com.ousl.lfs.ousl_lfs_backend.found.service;

import com.ousl.lfs.ousl_lfs_backend.common.util.CurrentUserService;
import com.ousl.lfs.ousl_lfs_backend.found.dto.FoundItemResponse;
import com.ousl.lfs.ousl_lfs_backend.found.model.FoundItemPhoto;
import com.ousl.lfs.ousl_lfs_backend.found.model.FoundItemReport;
import com.ousl.lfs.ousl_lfs_backend.found.repo.FoundItemReportRepository;
import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import com.ousl.lfs.ousl_lfs_backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoundItemService {

    private final FoundItemReportRepository repo;
    private final CurrentUserService currentUserService;
    private final FoundItemStorageService storage;

    @Transactional
    public FoundItemResponse createFoundReport(
            ItemCategory category,
            String description,
            String foundLocation,
            Instant foundAt,
            String serialNumberOrMarkings,
            List<MultipartFile> photos
    ) {
        User user = currentUserService.requireUser();

        FoundItemReport report = new FoundItemReport();
        report.setCategory(category);
        report.setDescription(description);
        report.setFoundLocation(foundLocation);
        report.setFoundAt(foundAt);
        report.setSerialNumberOrMarkings(serialNumberOrMarkings);
        report.setUser(user);

        // 1) Save first to get ID
        report = repo.save(report);

        // 2) Tracking number based on date + id (unique, concurrency-safe)
        report.setTrackingNumber(generateTrackingNumber(report.getId()));
        report = repo.save(report);

        // 3) Save photos
        List<String> savedPaths = storage.saveFoundPhotos(report.getTrackingNumber(), photos);
        for (String p : savedPaths) {
            FoundItemPhoto photo = new FoundItemPhoto();
            photo.setPath(p);
            report.addPhoto(photo);
        }

        // 4) Save report again (cascades photos)
        report = repo.save(report);

        return toResponse(report);
    }

    private String generateTrackingNumber(Long id) {
        String date = LocalDate.now(ZoneId.of("Asia/Colombo"))
                .format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
        return "FND-" + date + "-" + String.format("%06d", id);
    }

    private FoundItemResponse toResponse(FoundItemReport r) {
        List<String> photoPaths = (r.getPhotos() == null)
                ? List.of()
                : r.getPhotos().stream().map(FoundItemPhoto::getPath).toList();

        return new FoundItemResponse(
                r.getId(),
                r.getTrackingNumber(),
                r.getCategory(),
                r.getDescription(),
                r.getFoundLocation(),
                r.getFoundAt(),
                r.getSerialNumberOrMarkings(),
                photoPaths
        );
    }
}
