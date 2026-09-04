package com.shreespark.pos_api.tenant.service;

import com.shreespark.pos_api.common.enums.SubscriptionPlan;
import com.shreespark.pos_api.common.enums.TenantStatus;
import com.shreespark.pos_api.tenant.dto.request.CreateTenantRequest;
import com.shreespark.pos_api.tenant.dto.request.UpdateTenantRequest;
import com.shreespark.pos_api.tenant.dto.response.TenantResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TenantService {
    TenantResponse create(CreateTenantRequest request);
    TenantResponse getById(UUID id);
    List<TenantResponse> getAll();
    TenantResponse update(UUID id, UpdateTenantRequest request);
    void deactivate(UUID id);
    TenantResponse changeStatus(UUID id, TenantStatus status);
    TenantResponse changePlan(UUID id, SubscriptionPlan plan, LocalDate newExpiry);
}
