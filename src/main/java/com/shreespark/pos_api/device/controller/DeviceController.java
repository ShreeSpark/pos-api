package com.shreespark.pos_api.device.controller;

import com.shreespark.pos_api.auth.service.JwtService;
import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.device.dto.request.RegisterDeviceRequest;
import com.shreespark.pos_api.device.dto.response.DeviceResponse;
import com.shreespark.pos_api.device.service.DeviceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final JwtService jwtService;

    // Public auto-register for standalone app on first install
    @PostMapping("/public/register")
    public ResponseEntity<ApiResponse<DeviceResponse>> publicRegister(@Valid @RequestBody RegisterDeviceRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.publicRegister(request)));
    }

    // Public heartbeat for standalone app
    @GetMapping("/public/heartbeat/{deviceCode}")
    public ResponseEntity<ApiResponse<DeviceResponse>> publicHeartbeat(@PathVariable String deviceCode) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.publicHeartbeat(deviceCode)));
    }

    // Public key verification
    @PostMapping("/public/verify-key")
    public ResponseEntity<ApiResponse<DeviceResponse>> verifyKey(@RequestBody Map<String, String> body) {
        String deviceCode = body.get("deviceCode");
        String productKey = body.get("productKey");
        return ResponseEntity.ok(ApiResponse.ok(deviceService.verifyKey(deviceCode, productKey)));
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('DEVICE_MANAGE')")
    public ResponseEntity<ApiResponse<DeviceResponse>> register(
            @Valid @RequestBody RegisterDeviceRequest request,
            HttpServletRequest http) {
        UUID tenantId = extractTenantId(http);
        String staffId = jwtService.parseToken(extractToken(http)).getSubject();
        return ResponseEntity.ok(ApiResponse.ok(deviceService.register(tenantId, staffId, request)));
    }

    @PostMapping("/heartbeat/{deviceCode}")
    public ResponseEntity<ApiResponse<DeviceResponse>> heartbeat(
            @PathVariable String deviceCode, HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(
                deviceService.heartbeat(extractTenantId(http), deviceCode)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getAll(HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.getAll(extractTenantId(http))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceResponse>> getById(
            @PathVariable UUID id, HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.getById(extractTenantId(http), id)));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('DEVICE_MANAGE')")
    public ResponseEntity<ApiResponse<DeviceResponse>> approve(
            @PathVariable UUID id, HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.approve(extractTenantId(http), id)));
    }

    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasAuthority('DEVICE_MANAGE')")
    public ResponseEntity<ApiResponse<DeviceResponse>> suspend(
            @PathVariable UUID id, HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.suspend(extractTenantId(http), id)));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('DEVICE_MANAGE')")
    public ResponseEntity<ApiResponse<DeviceResponse>> activate(
            @PathVariable UUID id, HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.activate(extractTenantId(http), id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DEVICE_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> deregister(
            @PathVariable UUID id, HttpServletRequest http) {
        deviceService.deregister(extractTenantId(http), id);
        return ResponseEntity.ok(ApiResponse.ok("Device deregistered", null));
    }

    private UUID extractTenantId(HttpServletRequest http) {
        return jwtService.extractTenantId(extractToken(http));
    }

    private String extractToken(HttpServletRequest http) {
        return http.getHeader("Authorization").substring(7);
    }
}
