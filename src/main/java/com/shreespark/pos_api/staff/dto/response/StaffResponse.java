package com.shreespark.pos_api.staff.dto.response;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record StaffResponse(
        UUID id,
        UUID tenantId,
        String name,
        String email,
        String phone,
        String role,
        Set<String> permissions,
        boolean active,
        Instant createdAt
) {}
