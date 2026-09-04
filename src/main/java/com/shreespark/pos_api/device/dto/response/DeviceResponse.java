package com.shreespark.pos_api.device.dto.response;

import com.shreespark.pos_api.common.enums.DeviceStatus;

import java.time.Instant;
import java.util.UUID;

public record DeviceResponse(
        UUID id,
        String deviceCode,
        String deviceName,
        String platform,
        String appVersion,
        DeviceStatus status,
        Instant lastSeenAt,
        Instant createdAt
) {}
