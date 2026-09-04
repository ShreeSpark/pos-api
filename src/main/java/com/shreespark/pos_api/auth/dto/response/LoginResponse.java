package com.shreespark.pos_api.auth.dto.response;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        StaffProfile staff
) {
    public record StaffProfile(
            String id,
            String name,
            String email,
            String role,
            List<String> permissions,
            String tenantId
    ) {}
}
