package com.shreespark.pos_api.subscription.dto.request;

import com.shreespark.pos_api.common.enums.SubscriptionPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdatePlanConfigRequest(
        @NotBlank String displayName,
        @NotNull BigDecimal monthlyPrice,
        @NotNull BigDecimal yearlyPrice,
        @NotNull Integer maxDevices,
        @NotNull Integer maxStaff,
        @NotNull Integer maxProducts,
        String features
) {}
