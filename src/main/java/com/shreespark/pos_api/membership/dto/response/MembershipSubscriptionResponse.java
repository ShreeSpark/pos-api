package com.shreespark.pos_api.membership.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MembershipSubscriptionResponse(
        UUID id,
        UUID customerId,
        String customerName,
        String tier,
        String membershipName,
        BigDecimal discountPercent,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {}
