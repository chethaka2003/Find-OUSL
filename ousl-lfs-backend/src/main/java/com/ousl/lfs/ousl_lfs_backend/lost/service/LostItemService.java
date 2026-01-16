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

import java.io.InputStream;
import java.nio.file.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LostItemService {

    private final LostItemReportRepository reportRepo;
    private final LostItemPhotoRepository photoRepo;
    private final UserRepository userRepo;
    private final MailService mailService;

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
}
