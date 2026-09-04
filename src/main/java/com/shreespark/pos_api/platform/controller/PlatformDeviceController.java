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

    // Suspend a device platform-wide
    @PatchMapping("/{tenantId}/{deviceId}/suspend")
    public ResponseEntity<ApiResponse<DeviceResponse>> suspend(
            @PathVariable UUID tenantId, @PathVariable UUID deviceId) {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.suspend(tenantId, deviceId)));
    }
}
