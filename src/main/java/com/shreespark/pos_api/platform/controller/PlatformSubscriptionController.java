package com.shreespark.pos_api.platform.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.common.enums.SubscriptionPlan;
import com.shreespark.pos_api.subscription.dto.request.UpdatePlanConfigRequest;
import com.shreespark.pos_api.subscription.dto.response.SubscriptionPlanConfigResponse;
import com.shreespark.pos_api.subscription.service.SubscriptionPlanConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/platform/subscription-plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PlatformSubscriptionController {

    private final SubscriptionPlanConfigService planConfigService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionPlanConfigResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(planConfigService.getAll()));
    }

    @GetMapping("/{plan}")
    public ResponseEntity<ApiResponse<SubscriptionPlanConfigResponse>> getByPlan(
            @PathVariable SubscriptionPlan plan) {
        return ResponseEntity.ok(ApiResponse.ok(planConfigService.getByPlan(plan)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanConfigResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlanConfigRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(planConfigService.update(id, request)));
    }
}
