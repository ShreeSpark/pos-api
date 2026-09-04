package com.shreespark.pos_api.membership.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MembershipResponse(
        UUID id,
        String tier,
        String name,
        BigDecimal discountPercent,
        BigDecimal minPurchaseAmount,
        Integer validityDays,
        String description,
        Instant createdAt
) {}
