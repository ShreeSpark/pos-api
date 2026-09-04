package com.shreespark.pos_api.subscription.mapper;

import com.shreespark.pos_api.subscription.dto.response.SubscriptionPlanConfigResponse;
import com.shreespark.pos_api.subscription.entity.SubscriptionPlanConfig;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionPlanConfigMapper {
    public SubscriptionPlanConfigResponse toResponse(SubscriptionPlanConfig c) {
        return new SubscriptionPlanConfigResponse(
                c.getId(), c.getPlan(), c.getDisplayName(),
                c.getMonthlyPrice(), c.getYearlyPrice(),
                c.getMaxDevices(), c.getMaxStaff(), c.getMaxProducts(),
                c.getFeatures(), c.isActive()
        );
    }
}
