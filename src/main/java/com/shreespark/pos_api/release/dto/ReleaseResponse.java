package com.shreespark.pos_api.release.dto;

import java.time.Instant;
import java.util.UUID;

public record ReleaseResponse(
        UUID id,
        String targetApp,
        String version,
        Integer versionCode,
        String releaseNotes,
        String downloadUrl,
        boolean mandatory,
        boolean active,
        Instant createdAt
) {}
