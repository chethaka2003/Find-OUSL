package com.ousl.lfs.ousl_lfs_backend.lost.service;

import com.ousl.lfs.ousl_lfs_backend.auth.service.MailService;
import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemCreateResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemDashboardItemResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemListItemResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemPageResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemUpdateResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemAuditLog;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemPhoto;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostReportStatus;
import com.ousl.lfs.ousl_lfs_backend.lost.repo.LostItemAuditLogRepository;
import com.ousl.lfs.ousl_lfs_backend.lost.repo.LostItemPhotoRepository;
import com.ousl.lfs.ousl_lfs_backend.lost.repo.LostItemReportRepository;
import com.ousl.lfs.ousl_lfs_backend.lost.repo.LostItemSpecs;
import com.ousl.lfs.ousl_lfs_backend.match.service.MatchService;
import com.ousl.lfs.ousl_lfs_backend.user.model.User;
import com.ousl.lfs.ousl_lfs_backend.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LostItemService {

    private final LostItemReportRepository reportRepo;
    private final LostItemPhotoRepository photoRepo;
    private final UserRepository userRepo;
    private final MailService mailService;
    private final LostItemAuditLogRepository auditRepo;
    private final LostItemReportRepository lostItemReportRepository;

    // ✅ FR11 service (already injected correctly)
    private final MatchService matchService;

    @Value("${ulfs.lost.uploadDir:uploads/lost-items}")
    private String uploadDir;

    // =====================================================
    // FR5 — CREATE LOST ITEM (FR11 TRIGGER ADDED)
    // =====================================================
    public LostItemCreateResponse createReport(
            String userEmail,
            String categoryRaw,
            String description,
            String lostLocation,
            String lostAtIso,
            List<MultipartFile> photos
    ) {
        ItemCategory category = ItemCategory.from(categoryRaw);

        if (description == null || description.trim().length() < 20) {
            throw new IllegalArgumentException("Description must be at least 20 characters");
        }
        if (lostLocation == null || lostLocation.isBlank()) {
            throw new IllegalArgumentException("Lost location is required");
        }
        if (lostAtIso == null || lostAtIso.isBlank()) {
            throw new IllegalArgumentException("Lost date/time is required");
        }

        Instant lostAt = OffsetDateTime.parse(lostAtIso).toInstant();

        List<MultipartFile> safePhotos = (photos == null) ? List.of() : photos;
        if (safePhotos.size() > 3) {
            throw new IllegalArgumentException("You can upload up to 3 photos only");
        }

        for (MultipartFile f : safePhotos) {
            if (f == null || f.isEmpty()) continue;
            if (f.getSize() > 5L * 1024 * 1024) {
                throw new IllegalArgumentException("Each photo must be <= 5MB");
            }
        }

        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        LostItemReport report = new LostItemReport();
        report.setCategory(category);
        report.setDescription(description.trim());
        report.setLostLocation(lostLocation.trim());
        report.setLostAt(lostAt);
        report.setUser(user);
        report.setStatus(LostReportStatus.ACTIVE);

        report.setTrackingNumber(UUID.randomUUID().toString());
        report = reportRepo.save(report);

        String tracking = "LST-" +
                DateTimeFormatter.ofPattern("yyyyMMdd").format(OffsetDateTime.now()) +
                "-" + String.format("%06d", report.getId());

        report.setTrackingNumber(tracking);
        report = reportRepo.save(report);

        // =========================
        // ✅ FR11 AUTO MATCH TRIGGER
        // =========================
        matchService.matchForLostItem(report);

        List<String> savedPaths = new ArrayList<>();
        if (!safePhotos.isEmpty()) {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            try {
                Files.createDirectories(base.resolve(tracking));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to create upload directory", e);
            }

            for (MultipartFile f : safePhotos) {
                if (f == null || f.isEmpty()) continue;

                String ext = guessExt(f.getOriginalFilename(), f.getContentType());
                String fileName = UUID.randomUUID() + ext;
                Path target = base.resolve(tracking).resolve(fileName).normalize();

                try (InputStream in = f.getInputStream()) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to save photo", e);
                }

                LostItemPhoto p = new LostItemPhoto();
                p.setReport(report);
                p.setOriginalName(Optional.ofNullable(f.getOriginalFilename()).orElse("photo"));
                p.setFilePath(target.toString());
                photoRepo.save(p);

                savedPaths.add(target.toString());
            }
        }

        try {
            mailService.sendLostReportConfirmation(
                    user.getEmail(),
                    tracking,
                    category.name(),
                    report.getDescription(),
                    report.getLostLocation(),
                    report.getLostAt().toString()
            );
        } catch (Exception ignored) {}

        return new LostItemCreateResponse(
                report.getId(),
                report.getTrackingNumber(),
                report.getCategory().name(),
                report.getDescription(),
                report.getLostLocation(),
                report.getLostAt().toString(),
                savedPaths
        );
    }

    private String guessExt(String originalName, String contentType) {
        if (originalName == null) return "";
        String lower = originalName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return ".jpg";
        if (lower.endsWith(".png")) return ".png";
        if (lower.endsWith(".webp")) return ".webp";
        return "";
    }

    // =====================================================
    // FR6 — UPDATE LOST ITEM (FR11 TRIGGER ADDED)
    // =====================================================
    @Transactional
    public LostItemUpdateResponse updateLostItemDetails(
            Long reportId,
            String userEmail,
            String serialNumberOrMarkings,
            String distinguishingFeatures,
            Double estimatedValue,
            Double rewardAmount,
            List<MultipartFile> newPhotos
    ) {
        LostItemReport report = reportRepo.findWithUserById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Lost report not found"));

        if (!report.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new SecurityException("You are not allowed to edit this report");
        }

        if (Duration.between(report.getCreatedAt(), Instant.now()).toHours() > 24) {
            throw new IllegalStateException("Edit window has passed (24 hours)");
        }

        if (serialNumberOrMarkings != null) report.setSerialNumberOrMarkings(serialNumberOrMarkings);
        if (distinguishingFeatures != null) report.setDistinguishingFeatures(distinguishingFeatures);
        if (estimatedValue != null) report.setEstimatedValue(estimatedValue);
        if (rewardAmount != null) report.setRewardAmount(rewardAmount);

        reportRepo.save(report);

        // =========================
        // ✅ FR11 AUTO MATCH TRIGGER
        // =========================
        matchService.matchForLostItem(report);

        LostItemAuditLog log = new LostItemAuditLog();
        log.setReport(report);
        log.setAction("UPDATE_DETAILS");
        log.setUserEmail(userEmail);
        auditRepo.save(log);

        return new LostItemUpdateResponse(
                report.getId(),
                report.getTrackingNumber(),
                report.getSerialNumberOrMarkings(),
                report.getDistinguishingFeatures(),
                report.getEstimatedValue(),
                report.getRewardAmount(),
                List.of(),
                "Lost item details updated successfully."
        );
    }

    // ===========================
    // FR10 — SEARCH
    // ===========================
    @Transactional(readOnly = true)
    public Page<LostItemListItemResponse> searchLostItems(
            ItemCategory category,
            String q,
            OffsetDateTime from,
            OffsetDateTime to,
            Pageable pageable
    ) {
        Specification<LostItemReport> spec = Specification.allOf(
                LostItemSpecs.categoryIs(category),
                LostItemSpecs.keywordLike(q),
                LostItemSpecs.lostAtFrom(from),
                LostItemSpecs.lostAtTo(to)
        );

        return lostItemReportRepository.findAll(spec, pageable)
                .map(r -> new LostItemListItemResponse(
                        r.getId(),
                        r.getTrackingNumber(),
                        r.getCategory(),
                        r.getDescription(),
                        r.getLostLocation(),
                        r.getLostAt(),
                        extractPhotoPaths(r)
                ));
    }

    private List<String> extractPhotoPaths(LostItemReport r) {
        if (r.getPhotos() == null) return List.of();
        return r.getPhotos().stream().map(LostItemPhoto::getFilePath).toList();
    }

    // ===========================
    // FR7 — DASHBOARD + CANCEL
    // ===========================
    @Transactional(readOnly = true)
    public LostItemPageResponse<LostItemDashboardItemResponse> myReports(String email, Pageable pageable) {
        Page<LostItemReport> page = reportRepo.findByUser_Email(email, pageable);

        List<LostItemDashboardItemResponse> content = page.getContent().stream()
                .map(r -> new LostItemDashboardItemResponse(
                        r.getId(),
                        r.getTrackingNumber(),
                        r.getCategory(),
                        r.getDescription(),
                        r.getLostLocation(),
                        r.getLostAt(),
                        r.getStatus(),
                        r.getCreatedAt(),
                        extractPhotoPaths(r)
                ))
                .toList();

        return new LostItemPageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Transactional
    public void cancelMyReport(String email, Long reportId) {
        LostItemReport report = reportRepo.findWithUserById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Lost report not found"));

        if (!report.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new SecurityException("You are not allowed to cancel this report");
        }

        report.setStatus(LostReportStatus.ARCHIVED);
        reportRepo.save(report);
    }
}
