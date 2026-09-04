package com.shreespark.pos_api.subscription.service;

import com.shreespark.pos_api.common.enums.SubscriptionPlan;
import com.shreespark.pos_api.subscription.dto.request.UpdatePlanConfigRequest;
import com.shreespark.pos_api.subscription.dto.response.SubscriptionPlanConfigResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanConfigService {
    List<SubscriptionPlanConfigResponse> getAll();
    SubscriptionPlanConfigResponse getByPlan(SubscriptionPlan plan);
    SubscriptionPlanConfigResponse update(UUID id, UpdatePlanConfigRequest request);
}
