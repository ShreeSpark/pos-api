package com.shreespark.pos_api.device.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterDeviceRequest(
        @NotBlank String deviceCode,
        @NotBlank String deviceName,
        String platform,
        String appVersion
) {}
