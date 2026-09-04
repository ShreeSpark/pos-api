package com.shreespark.pos_api.common.service.impl;

import com.shreespark.pos_api.common.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public String store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) throw new RuntimeException("File is empty");

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new RuntimeException("Invalid file type. Allowed: jpeg, png, webp");
        }

        try {
            String extension = getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + "." + extension;
            Path targetDir = Paths.get(uploadDir, folder);
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return folder + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    @Override
    public void delete(String filePath) {
        if (filePath == null) return;
        try {
            Path path = Paths.get(uploadDir, filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // log but don't throw — deletion failure is non-critical
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
