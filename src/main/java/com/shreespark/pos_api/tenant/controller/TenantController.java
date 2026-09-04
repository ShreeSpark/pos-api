package com.shreespark.pos_api.tenant.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.tenant.dto.request.CreateTenantRequest;
import com.shreespark.pos_api.tenant.dto.request.UpdateTenantRequest;
import com.shreespark.pos_api.tenant.dto.response.TenantResponse;
import com.shreespark.pos_api.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/platform/tenant-management")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponse>> create(
            @Valid @RequestBody CreateTenantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tenant created", tenantService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponse>> update(
            @PathVariable UUID id,
            @RequestBody UpdateTenantRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Tenant updated", tenantService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        tenantService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Tenant deactivated", null));
    }
}
