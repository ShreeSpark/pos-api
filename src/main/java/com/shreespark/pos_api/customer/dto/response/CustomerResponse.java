package com.shreespark.pos_api.customer.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String phone,
        String email,
        String address,
        String gstin,
        String type,
        BigDecimal creditLimit,
        BigDecimal outstandingBalance,
        boolean creditExceeded,
        ActiveMembership activeMembership,
        Instant createdAt
) {
    public record ActiveMembership(
            UUID subscriptionId,
            UUID membershipId,
            String tier,
            String membershipName,
            BigDecimal discountPercent,
            LocalDate startDate,
            LocalDate endDate,
            String status
    ) {}
}
