package com.shreespark.pos_api.membership.dto.request;

import java.math.BigDecimal;

public record UpdateMembershipRequest(
        String name,
        BigDecimal discountPercent,
        BigDecimal minPurchaseAmount,
        Integer validityDays,
        String description
) {}
