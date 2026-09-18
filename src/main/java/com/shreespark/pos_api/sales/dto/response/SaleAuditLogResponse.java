package com.shreespark.pos_api.sales.dto.response;

import java.time.Instant;
import java.util.UUID;

public record SaleAuditLogResponse(
        UUID id,
        UUID saleId,
        String action,
        UUID staffId,
        String staffName,
        String details,
        Instant createdAt
) {}
