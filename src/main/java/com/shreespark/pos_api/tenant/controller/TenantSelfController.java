package com.shreespark.pos_api.tenant.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.tenant.dto.response.TenantResponse;
import com.shreespark.pos_api.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class TenantSelfController {

    private final TenantService tenantService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TenantResponse>> getMe(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.getById(tenantId)));
    }
}
