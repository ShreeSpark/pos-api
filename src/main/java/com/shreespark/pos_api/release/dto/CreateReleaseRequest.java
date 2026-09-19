package com.shreespark.pos_api.release.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReleaseRequest(
        @NotBlank String targetApp,
        @NotBlank String version,
        @NotNull Integer versionCode,
        String releaseNotes,
        @NotBlank String downloadUrl,
        boolean mandatory
) {}
