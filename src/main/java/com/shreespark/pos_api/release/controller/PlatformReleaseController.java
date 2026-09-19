package com.shreespark.pos_api.release.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.release.dto.CreateReleaseRequest;
import com.shreespark.pos_api.release.dto.ReleaseResponse;
import com.shreespark.pos_api.release.entity.AppRelease;
import com.shreespark.pos_api.release.repository.AppReleaseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;
import com.shreespark.pos_api.common.service.FileStorageService;

@RestController
@RequestMapping("/platform/releases")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PlatformReleaseController {

    private final AppReleaseRepository releaseRepository;
    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadInstallerFile(
            @RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.store(file, "releases");
        return ResponseEntity.ok(ApiResponse.ok("Installer uploaded successfully", fileUrl));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReleaseResponse>>> getAllReleases() {
        var list = releaseRepository.findAllByActiveTrueOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReleaseResponse>> publishRelease(
            @Valid @RequestBody CreateReleaseRequest req) {
        AppRelease release = AppRelease.builder()
                .targetApp(req.targetApp())
                .version(req.version())
                .versionCode(req.versionCode())
                .releaseNotes(req.releaseNotes())
                .downloadUrl(req.downloadUrl())
                .mandatory(req.mandatory())
                .active(true)
                .build();
        return ResponseEntity.ok(ApiResponse.ok(toResponse(releaseRepository.save(release))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRelease(@PathVariable UUID id) {
        var rel = releaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Release not found: " + id));
        rel.setActive(false);
        releaseRepository.save(rel);
        return ResponseEntity.ok(ApiResponse.ok("Release revoked", null));
    }

    private ReleaseResponse toResponse(AppRelease r) {
        return new ReleaseResponse(
                r.getId(),
                r.getTargetApp(),
                r.getVersion(),
                r.getVersionCode(),
                r.getReleaseNotes(),
                r.getDownloadUrl(),
                r.isMandatory(),
                r.isActive(),
                r.getCreatedAt()
        );
    }
}
