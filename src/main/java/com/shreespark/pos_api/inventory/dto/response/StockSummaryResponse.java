package com.shreespark.pos_api.inventory.dto.response;

import java.util.UUID;

public record StockSummaryResponse(
        UUID productId,
        String productName,
        String sku,
        Integer stockQuantity,
        Integer lowStockThreshold,
        Integer moq,
        boolean lowStock
) {}
