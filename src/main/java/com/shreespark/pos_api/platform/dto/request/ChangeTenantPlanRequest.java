package com.shreespark.pos_api.platform.dto.request;

import com.shreespark.pos_api.common.enums.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ChangeTenantPlanRequest(
        @NotNull SubscriptionPlan plan,
        @NotNull LocalDate newExpiry
) {}
