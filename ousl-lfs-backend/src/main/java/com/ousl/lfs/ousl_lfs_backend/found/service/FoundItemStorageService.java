package com.ousl.lfs.ousl_lfs_backend.found.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FoundItemStorageService {

    // you can configure in application.properties
    @Value("${app.uploads.dir:uploads}")
    private String uploadsDir;

    public List<String> saveFoundPhotos(String trackingNumber, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return List.of();

        List<String> saved = new ArrayList<>();
        Path base = Paths.get(uploadsDir, "found-items", trackingNumber);

        try {
            Files.createDirectories(base);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create upload directory: " + base, e);
        }

        for (MultipartFile f : files) {
            if (f == null || f.isEmpty()) continue;

            String cleanName = sanitizeFilename(f.getOriginalFilename());
            String filename = System.currentTimeMillis() + "_" + cleanName;
            Path target = base.resolve(filename);

            try {
                Files.copy(f.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save file: " + filename, e);
            }

            // return relative path (nice for frontend)
            saved.add(Paths.get("found-items", trackingNumber, filename).toString().replace("\\", "/"));
        }

        return saved;
    }

    private String sanitizeFilename(String name) {
        if (name == null || name.isBlank()) return "file";
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
