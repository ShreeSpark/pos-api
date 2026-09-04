package com.shreespark.pos_api.inventory.dto.response;

import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        UUID productId,
        String productName,
        String type,
        Integer quantity,
        Integer stockBefore,
        Integer stockAfter,
        String referenceId,
        String note,
        Instant createdAt
) {}
