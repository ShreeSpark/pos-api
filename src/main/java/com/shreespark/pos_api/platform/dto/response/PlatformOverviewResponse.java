package com.shreespark.pos_api.platform.dto.response;

public record PlatformOverviewResponse(
        long totalTenants,
        long activeTenants,
        long suspendedTenants,
        long expiringThisWeek,
        long totalDevices
) {}
