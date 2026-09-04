package com.shreespark.pos_api.tenant.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TenantResponse(
        UUID id,
        String businessName,
        String email,
        String phone,
        String address,
        String gstin,
        String status,
        String subscriptionPlan,
        LocalDate subscriptionExpiry,
        Instant createdAt
) {}
