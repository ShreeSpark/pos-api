package com.shreespark.pos_api.platform.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.device.dto.response.DeviceResponse;
import com.shreespark.pos_api.device.mapper.DeviceMapper;
import com.shreespark.pos_api.device.repository.DeviceRepository;
import com.shreespark.pos_api.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/platform/devices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PlatformDeviceController {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;
    private final DeviceService deviceService;

    // View all devices across all tenants
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getAll() {
        List<DeviceResponse> devices = deviceRepository.findAll()
                .stream().map(deviceMapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(devices));
    }

    // View all devices for a specific tenant
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getByTenant(
            @PathVariable UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.getAll(tenantId)));
    }

    // Approve a pending device
    @PatchMapping("/{tenantId}/{deviceId}/approve")
    public ResponseEntity<ApiResponse<DeviceResponse>> approve(
            @PathVariable UUID tenantId, @PathVariable UUID deviceId) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.approve(tenantId, deviceId)));
    }

    // Suspend a device platform-wide
    @PatchMapping("/{tenantId}/{deviceId}/suspend")
    public ResponseEntity<ApiResponse<DeviceResponse>> suspend(
            @PathVariable UUID tenantId, @PathVariable UUID deviceId) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.suspend(tenantId, deviceId)));
    }

    // Terminate a device platform-wide (Permanent Revocation)
    @PatchMapping("/{tenantId}/{deviceId}/terminate")
    public ResponseEntity<ApiResponse<DeviceResponse>> terminate(
            @PathVariable UUID tenantId, @PathVariable UUID deviceId) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.terminate(tenantId, deviceId)));
    }

    // Generate 25-character activation key for device (with optional validity days or custom expiry date)
    @PostMapping("/{tenantId}/{deviceId}/generate-key")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateKey(
            @PathVariable UUID tenantId,
            @PathVariable UUID deviceId,
            @RequestBody(required = false) Map<String, Object> body) {
        Integer validDays = null;
        String customExpiryDate = null;

        if (body != null) {
            if (body.get("validDays") != null) {
                try {
                    validDays = Integer.parseInt(body.get("validDays").toString());
                } catch (Exception ignored) {}
            }
            if (body.get("customExpiryDate") != null) {
                customExpiryDate = body.get("customExpiryDate").toString();
            }
        }

        String key = deviceService.generateProductKey(tenantId, deviceId, validDays, customExpiryDate);
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "productKey", key,
                "message", "25-character Product Key generated successfully"
        )));
    }

    // Delete a device platform-wide (Permanent Deletion from DB)
    @DeleteMapping("/{tenantId}/{deviceId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteDevice(
            @PathVariable UUID tenantId, @PathVariable UUID deviceId) {
        deviceService.deleteDevice(tenantId, deviceId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("message", "Device deleted successfully")));
    }
}
