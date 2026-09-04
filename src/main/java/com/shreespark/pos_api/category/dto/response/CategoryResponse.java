package com.shreespark.pos_api.category.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String description,
        String imageUrl,
        String hsnCode,
        GstRateSummary gstRate,
        Instant createdAt
) {
    public record GstRateSummary(
            UUID id,
            String name,
            BigDecimal rate,
            BigDecimal cgstRate,
            BigDecimal sgstRate,
            BigDecimal igstRate
    ) {}
}
