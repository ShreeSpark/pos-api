package com.shreespark.pos_api.inventory.dto.response;

import java.time.Instant;
import java.util.UUID;

public record StockAdjustmentResponse(
        UUID id,
        UUID productId,
        String productName,
        Integer adjustedQuantity,
        Integer stockBefore,
        Integer stockAfter,
        String reason,
        String approvedBy,
        Instant createdAt
) {}
