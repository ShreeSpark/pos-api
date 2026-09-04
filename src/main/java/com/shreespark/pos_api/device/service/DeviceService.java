package com.shreespark.pos_api.device.service;

import com.shreespark.pos_api.device.dto.request.RegisterDeviceRequest;
import com.shreespark.pos_api.device.dto.response.DeviceResponse;

import java.util.List;
import java.util.UUID;

public interface DeviceService {
    DeviceResponse register(UUID tenantId, String registeredBy, RegisterDeviceRequest request);
    DeviceResponse heartbeat(UUID tenantId, String deviceCode);
    DeviceResponse suspend(UUID tenantId, UUID deviceId);
    DeviceResponse activate(UUID tenantId, UUID deviceId);
    void deregister(UUID tenantId, UUID deviceId);
    List<DeviceResponse> getAll(UUID tenantId);
    DeviceResponse getById(UUID tenantId, UUID deviceId);
}
