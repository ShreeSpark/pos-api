package com.shreespark.pos_api.product.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        String sku,
        String imageUrl,
        BigDecimal retailPrice,
        BigDecimal wholesalePrice,
        BigDecimal dealerPrice,
        BigDecimal costPrice,
        Integer lowStockThreshold,
        Integer moq,
        CategorySummary category,
        BrandSummary brand,
        List<BarcodeSummary> barcodes,
        Instant createdAt
) {
    public record CategorySummary(
            UUID id,
            String name,
            String hsnCode,
            String gstRateName,
            BigDecimal gstPercent,
            BigDecimal cgstPercent,
            BigDecimal sgstPercent,
            BigDecimal igstPercent
    ) {}

    public record BrandSummary(UUID id, String name) {}
    public record BarcodeSummary(UUID id, String value, String format) {}
}
