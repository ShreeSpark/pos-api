package com.shreespark.pos_api.platform.dto.request;

import com.shreespark.pos_api.common.enums.TenantStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeTenantStatusRequest(
        @NotNull TenantStatus status
) {}
