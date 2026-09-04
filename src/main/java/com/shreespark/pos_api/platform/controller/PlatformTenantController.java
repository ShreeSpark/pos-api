package com.shreespark.pos_api.platform.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.platform.dto.request.ChangeTenantPlanRequest;
import com.shreespark.pos_api.platform.dto.request.ChangeTenantStatusRequest;
import com.shreespark.pos_api.platform.dto.response.PlatformOverviewResponse;
import com.shreespark.pos_api.common.enums.TenantStatus;
import com.shreespark.pos_api.device.repository.DeviceRepository;
import com.shreespark.pos_api.tenant.dto.request.CreateTenantRequest;
import com.shreespark.pos_api.tenant.dto.response.TenantResponse;
import com.shreespark.pos_api.tenant.repository.TenantRepository;
import com.shreespark.pos_api.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/platform/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PlatformTenantController {

    private final TenantService tenantService;
    private final TenantRepository tenantRepository;
    private final DeviceRepository deviceRepository;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<PlatformOverviewResponse>> overview() {
        long total     = tenantRepository.count();
        long active    = tenantRepository.countByStatus(TenantStatus.ACTIVE);
        long suspended = tenantRepository.countByStatus(TenantStatus.SUSPENDED);
        long expiring  = tenantRepository.findExpiringBetween(LocalDate.now(), LocalDate.now().plusDays(7)).size();
        long devices   = deviceRepository.count();
        return ResponseEntity.ok(ApiResponse.ok(
                new PlatformOverviewResponse(total, active, suspended, expiring, devices)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponse>> create(
            @Valid @RequestBody CreateTenantRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.create(request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TenantResponse>> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeTenantStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                tenantService.changeStatus(id, request.status())));
    }

    @PatchMapping("/{id}/plan")
    public ResponseEntity<ApiResponse<TenantResponse>> changePlan(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeTenantPlanRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                tenantService.changePlan(id, request.plan(), request.newExpiry())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        tenantService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Tenant deactivated", null));
    }
}
