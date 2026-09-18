package com.shreespark.pos_api.subscription.service;

import com.shreespark.pos_api.subscription.entity.SubscriptionPlanConfig;
import com.shreespark.pos_api.subscription.repository.SubscriptionPlanConfigRepository;
import com.shreespark.pos_api.tenant.entity.Tenant;
import com.shreespark.pos_api.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanLimitService {

    private final TenantRepository tenantRepository;
    private final SubscriptionPlanConfigRepository planConfigRepository;

    public void checkLimit(UUID tenantId, LimitType type, long currentCount) {
        Tenant tenant = tenantRepository.findByIdAndActiveTrue(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));

        SubscriptionPlanConfig config = planConfigRepository.findByPlan(tenant.getSubscriptionPlan())
                .orElseThrow(() -> new RuntimeException("Plan config not found for: " + tenant.getSubscriptionPlan()));

        int limit = switch (type) {
            case STAFF -> config.getMaxStaff();
            case PRODUCTS -> config.getMaxProducts();
            case DEVICES -> config.getMaxDevices();
        };

        if (limit != -1 && currentCount >= limit) {
            throw new RuntimeException("Plan limit reached: cannot add more " + type.name().toLowerCase() +
                    ". Upgrade your plan to add more. (limit: " + limit + ")");
        }
    }

    public enum LimitType { STAFF, PRODUCTS, DEVICES }
}
