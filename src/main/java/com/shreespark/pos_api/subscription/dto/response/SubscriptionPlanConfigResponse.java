package com.shreespark.pos_api.subscription.dto.response;

import com.shreespark.pos_api.common.enums.SubscriptionPlan;

import java.math.BigDecimal;
import java.util.UUID;

public record SubscriptionPlanConfigResponse(
        UUID id,
        SubscriptionPlan plan,
        String displayName,
        BigDecimal monthlyPrice,
        BigDecimal yearlyPrice,
        int maxDevices,
        int maxStaff,
        int maxProducts,
        String features,
        boolean active
) {}
