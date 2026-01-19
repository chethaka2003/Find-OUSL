package com.ousl.lfs.ousl_lfs_backend.lost.service;

import com.ousl.lfs.ousl_lfs_backend.auth.service.MailService;
import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemCreateResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.model.ItemCategory;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemPhoto;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport;
import com.ousl.lfs.ousl_lfs_backend.lost.repo.LostItemPhotoRepository;
import com.ousl.lfs.ousl_lfs_backend.lost.repo.LostItemReportRepository;
import com.ousl.lfs.ousl_lfs_backend.user.model.User;
import com.ousl.lfs.ousl_lfs_backend.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemUpdateResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemAuditLog;
import com.ousl.lfs.ousl_lfs_backend.lost.repo.LostItemAuditLogRepository;
import java.time.Duration;
import org.springframework.transaction.annotation.Transactional;

import com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemListItemResponse;
import com.ousl.lfs.ousl_lfs_backend.lost.repo.LostItemSpecs;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;


import java.io.InputStream;
import java.nio.file.*;
import java.time.Instant;
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


    @Value("${ulfs.lost.uploadDir:uploads/lost-items}")
    private String uploadDir;

    public LostItemCreateResponse createReport(
            String userEmail,
            String categoryRaw,
            String description,
            String lostLocation,
            String lostAtIso,
            List<MultipartFile> photos
    ) {
        // ---- Validations from SRS ----
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

        // Photos 0–3, max 5MB each
        List<MultipartFile> safePhotos = (photos == null) ? List.of() : photos;
        if (safePhotos.size() > 3) {
            throw new IllegalArgumentException("You can upload up to 3 photos only");
        }

        for (MultipartFile f : safePhotos) {
            if (f == null || f.isEmpty()) continue;
            if (f.getSize() > 5L * 1024 * 1024) {
                throw new IllegalArgumentException("Each photo must be <= 5MB");
            }
            String ct = f.getContentType();
            if (ct == null || !(ct.equalsIgnoreCase("image/jpeg") || ct.equalsIgnoreCase("image/png") || ct.equalsIgnoreCase("image/webp"))) {
                throw new IllegalArgumentException("Only JPG/PNG/WEBP images are allowed");
            }
        }

        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        // ---- Save report first to get DB ID ----
        LostItemReport report = new LostItemReport();
        report.setCategory(category);
        report.setDescription(description.trim());
        report.setLostLocation(lostLocation.trim());
        report.setLostAt(lostAt);
        report.setUser(user);

        // temporary tracking (will update after id exists)
        report.setTrackingNumber(UUID.randomUUID().toString());
        report = reportRepo.save(report);

        // Generate tracking number: LST-YYYYMMDD-000001
        String tracking = "LST-" + DateTimeFormatter.ofPattern("yyyyMMdd").format(OffsetDateTime.now())
                + "-" + String.format("%06d", report.getId());
        report.setTrackingNumber(tracking);
        report = reportRepo.save(report);

        // ---- Save files ----
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
                    throw new IllegalStateException("Failed to save photo: " + f.getOriginalFilename(), e);
                }

                LostItemPhoto p = new LostItemPhoto();
                p.setReport(report);
                p.setOriginalName(Optional.ofNullable(f.getOriginalFilename()).orElse("photo"));
                p.setFilePath(target.toString());
                photoRepo.save(p);

                savedPaths.add(target.toString());
            }
        }

        // ---- Email confirmation (do NOT fail request if mail fails) ----
        try {
            mailService.sendLostReportConfirmation(
                    user.getEmail(),
                    tracking,
                    category.name(),
                    report.getDescription(),
                    report.getLostLocation(),
                    report.getLostAt().toString()
            );
        } catch (Exception ignored) {
            // SRS says queue/retry if SMTP unavailable; for now we don't break the request.
        }

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
        String lower = (originalName == null) ? "" : originalName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return ".jpg";
        if (lower.endsWith(".png")) return ".png";
        if (lower.endsWith(".webp")) return ".webp";

        if (contentType == null) return "";
        if (contentType.equalsIgnoreCase("image/jpeg")) return ".jpg";
        if (contentType.equalsIgnoreCase("image/png")) return ".png";
        if (contentType.equalsIgnoreCase("image/webp")) return ".webp";
        return "";
    }

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
        // ✅ Fetch report WITH user (avoids LazyInitializationException)
        LostItemReport report = reportRepo.findWithUserById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Lost report not found"));

        // ✅ Permission check (only owner can edit)
        String ownerEmail = report.getUser().getEmail(); // now safe
        if (!ownerEmail.equalsIgnoreCase(userEmail)) {
            throw new SecurityException("You are not allowed to edit this report");
        }

        // ✅ 24 hour edit window check
        if (report.getCreatedAt() == null ||
                Duration.between(report.getCreatedAt(), Instant.now()).toHours() > 24) {
            throw new IllegalStateException("Edit window has passed (24 hours)");
        }

        // ✅ Validate numeric fields
        if (estimatedValue != null && estimatedValue < 0) {
            throw new IllegalArgumentException("Estimated value must be positive");
        }
        if (rewardAmount != null && rewardAmount < 0) {
            throw new IllegalArgumentException("Reward amount must be positive");
        }

        StringBuilder changes = new StringBuilder();

        // ✅ Update fields only if provided
        if (serialNumberOrMarkings != null && !serialNumberOrMarkings.isBlank()) {
            report.setSerialNumberOrMarkings(serialNumberOrMarkings.trim());
            changes.append("serialNumberOrMarkings updated; ");
        }

        if (distinguishingFeatures != null && !distinguishingFeatures.isBlank()) {
            report.setDistinguishingFeatures(distinguishingFeatures.trim());
            changes.append("distinguishingFeatures updated; ");
        }

        if (estimatedValue != null) {
            report.setEstimatedValue(estimatedValue);
            changes.append("estimatedValue updated; ");
        }

        if (rewardAmount != null) {
            report.setRewardAmount(rewardAmount);
            changes.append("rewardAmount updated; ");
        }

        // ✅ Save updated report
        reportRepo.save(report);

        // ✅ Optional: save extra photos
        List<MultipartFile> safePhotos = (newPhotos == null) ? List.of() : newPhotos;
        List<String> savedPaths = new ArrayList<>();

        if (!safePhotos.isEmpty()) {
            if (safePhotos.size() > 3) {
                throw new IllegalArgumentException("You can upload up to 3 photos at a time");
            }

            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            try {
                Files.createDirectories(base.resolve(report.getTrackingNumber()));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to create upload directory", e);
            }

            for (MultipartFile f : safePhotos) {
                if (f == null || f.isEmpty()) continue;

                if (f.getSize() > 5L * 1024 * 1024) {
                    throw new IllegalArgumentException("Each photo must be <= 5MB");
                }

                String ct = f.getContentType();
                if (ct == null || !(ct.equalsIgnoreCase("image/jpeg")
                        || ct.equalsIgnoreCase("image/png")
                        || ct.equalsIgnoreCase("image/webp"))) {
                    throw new IllegalArgumentException("Only JPG/PNG/WEBP images are allowed");
                }

                String ext = guessExt(f.getOriginalFilename(), f.getContentType());
                String fileName = UUID.randomUUID() + ext;
                Path target = base.resolve(report.getTrackingNumber()).resolve(fileName).normalize();

                try (InputStream in = f.getInputStream()) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to save photo: " + f.getOriginalFilename(), e);
                }

                LostItemPhoto p = new LostItemPhoto();
                p.setReport(report);
                p.setOriginalName(Optional.ofNullable(f.getOriginalFilename()).orElse("photo"));
                p.setFilePath(target.toString());
                photoRepo.save(p);

                savedPaths.add(target.toString());
            }

            if (!savedPaths.isEmpty()) {
                changes.append("addedPhotos=").append(savedPaths.size()).append("; ");
            }
        }

        // ✅ Audit log (FR6 requirement)
        LostItemAuditLog log = new LostItemAuditLog();
        log.setReport(report);
        log.setAction("UPDATE_DETAILS");
        log.setUserEmail(userEmail);
        log.setDetails(changes.toString());
        auditRepo.save(log);

        return new LostItemUpdateResponse(
                report.getId(),
                report.getTrackingNumber(),
                report.getSerialNumberOrMarkings(),
                report.getDistinguishingFeatures(),
                report.getEstimatedValue(),
                report.getRewardAmount(),
                savedPaths,
                "Lost item details updated successfully."
        );
    }


    /**
     * FR7: Search + filtering + pagination
     */
    @Transactional(readOnly = true)
    public Page<LostItemListItemResponse> searchLostItems(
            ItemCategory category,
            String q,
            OffsetDateTime from,
            OffsetDateTime to,
            Pageable pageable
    ) {
        // Spring Data JPA 3.5+: avoid deprecated Specification.where(...)
        Specification<LostItemReport> spec = Specification.allOf(
                LostItemSpecs.categoryIs(category),
                LostItemSpecs.keywordLike(q),
                LostItemSpecs.lostAtFrom(from),
                LostItemSpecs.lostAtTo(to)
        );

        return lostItemReportRepository.findAll(spec, pageable)
                .map(r -> new com.ousl.lfs.ousl_lfs_backend.lost.dto.LostItemListItemResponse(
                        r.getId(),
                        r.getTrackingNumber(),
                        r.getCategory(),
                        r.getDescription(),
                        r.getLostLocation(),
                        r.getLostAt(),
                        extractPhotoPaths(r)
                ));


    }

    private OffsetDateTime toOffset(Instant instant) {
        if (instant == null) return null;
        // Use Sri Lanka time (or use ZoneOffset.UTC if you prefer)
        return instant.atZone(ZoneId.of("Asia/Colombo")).toOffsetDateTime();
    }

    private java.util.List<String> extractPhotoPaths(com.ousl.lfs.ousl_lfs_backend.lost.model.LostItemReport r) {
        if (r.getPhotos() == null) return java.util.List.of();
        return r.getPhotos().stream()
                .map(p -> p.getFilePath())
                .toList();
    }



}
