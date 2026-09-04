package com.shreespark.pos_api.admin.dto;

public record AdminLoginResponse(
        String accessToken,
        String refreshToken,
        AdminProfile admin
) {
    public record AdminProfile(
            String id,
            String name,
            String email
    ) {}
}
