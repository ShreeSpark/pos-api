package com.shreespark.pos_api.brand.dto.response;

import java.time.Instant;
import java.util.UUID;

public record BrandResponse(
        UUID id,
        String name,
        String description,
        String imageUrl,
        Instant createdAt
) {}
