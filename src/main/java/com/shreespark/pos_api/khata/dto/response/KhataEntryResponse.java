package com.shreespark.pos_api.khata.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record KhataEntryResponse(
        UUID id,
        UUID customerId,
        String customerName,
        String type,
        BigDecimal amount,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        UUID referenceId,
        String note,
        Instant createdAt
) {}
