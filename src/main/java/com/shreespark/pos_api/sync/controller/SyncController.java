package com.shreespark.pos_api.sync.controller;

import com.shreespark.pos_api.auth.service.JwtService;
import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.sync.dto.request.SyncBatchRequest;
import com.shreespark.pos_api.sync.dto.response.SyncBatchResponse;
import com.shreespark.pos_api.sync.service.SyncService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;
    private final JwtService jwtService;

    // Device pushes offline operations in batch
    @PostMapping("/push/{deviceId}")
    public ResponseEntity<ApiResponse<SyncBatchResponse>> push(
            @PathVariable UUID deviceId,
            @Valid @RequestBody SyncBatchRequest request,
            HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(
                syncService.processBatch(tenantId(http), deviceId, request)));
    }

    // Get sync history for a device
    @GetMapping("/history/{deviceId}")
    public ResponseEntity<ApiResponse<SyncBatchResponse>> history(
            @PathVariable UUID deviceId, HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(
                syncService.getDeviceHistory(tenantId(http), deviceId)));
    }

    private UUID tenantId(HttpServletRequest http) {
        return jwtService.extractTenantId(http.getHeader("Authorization").substring(7));
    }
}
