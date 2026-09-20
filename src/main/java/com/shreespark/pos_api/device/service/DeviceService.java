package com.shreespark.pos_api.device.service;

import com.shreespark.pos_api.device.dto.request.RegisterDeviceRequest;
import com.shreespark.pos_api.device.dto.response.DeviceResponse;

import java.util.List;
import java.util.UUID;

public interface DeviceService {
    DeviceResponse register(UUID tenantId, String registeredBy, RegisterDeviceRequest request);
    DeviceResponse heartbeat(UUID tenantId, String deviceCode);
    DeviceResponse approve(UUID tenantId, UUID deviceId);
    DeviceResponse suspend(UUID tenantId, UUID deviceId);
    DeviceResponse terminate(UUID tenantId, UUID deviceId);
    DeviceResponse activate(UUID tenantId, UUID deviceId);
    String generateProductKey(UUID tenantId, UUID deviceId);
    void deregister(UUID tenantId, UUID deviceId);
    List<DeviceResponse> getAll(UUID tenantId);
    List<DeviceResponse> getPending(UUID tenantId);
    DeviceResponse getById(UUID tenantId, UUID deviceId);

    // Public standalone endpoints
    DeviceResponse publicRegister(RegisterDeviceRequest request);
    DeviceResponse publicHeartbeat(String deviceCode);
    DeviceResponse verifyKey(String deviceCode, String productKey);
}
