package com.shreespark.pos_api.subscription.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.common.enums.SubscriptionPlan;
import com.shreespark.pos_api.subscription.dto.response.SubscriptionPlanConfigResponse;
import com.shreespark.pos_api.subscription.service.SubscriptionPlanConfigService;
import com.shreespark.pos_api.tenant.entity.Tenant;
import com.shreespark.pos_api.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanConfigService planConfigService;
    private final TenantRepository tenantRepository;

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<SubscriptionPlanConfigResponse>> getMyPlan(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        Tenant tenant = tenantRepository.findByIdAndActiveTrue(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        return ResponseEntity.ok(ApiResponse.ok(planConfigService.getByPlan(tenant.getSubscriptionPlan())));
    }
}
