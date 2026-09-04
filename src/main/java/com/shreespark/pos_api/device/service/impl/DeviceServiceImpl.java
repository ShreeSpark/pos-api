package com.shreespark.pos_api.device.service.impl;

import com.shreespark.pos_api.common.enums.DeviceStatus;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.device.dto.request.RegisterDeviceRequest;
import com.shreespark.pos_api.device.dto.response.DeviceResponse;
import com.shreespark.pos_api.device.entity.Device;
import com.shreespark.pos_api.device.mapper.DeviceMapper;
import com.shreespark.pos_api.device.repository.DeviceRepository;
import com.shreespark.pos_api.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    @Override
    @Transactional
    public DeviceResponse register(UUID tenantId, String registeredBy, RegisterDeviceRequest req) {
        if (deviceRepository.existsByDeviceCodeAndTenantId(req.deviceCode(), tenantId)) {
            throw new RuntimeException("Device already registered: " + req.deviceCode());
        }
        Device device = Device.builder()
                .deviceCode(req.deviceCode())
                .deviceName(req.deviceName())
                .platform(req.platform())
                .appVersion(req.appVersion())
                .status(DeviceStatus.ACTIVE)
                .registeredBy(registeredBy)
                .lastSeenAt(Instant.now())
                .build();
        device.setTenantId(tenantId);
        return deviceMapper.toResponse(deviceRepository.save(device));
    }

    @Override
    @Transactional
    public DeviceResponse heartbeat(UUID tenantId, String deviceCode) {
        Device device = deviceRepository.findByDeviceCodeAndTenantId(deviceCode, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Device", deviceCode));
        device.setLastSeenAt(Instant.now());
        return deviceMapper.toResponse(deviceRepository.save(device));
    }

    @Override
    @Transactional
    public DeviceResponse suspend(UUID tenantId, UUID deviceId) {
        Device device = findOrThrow(tenantId, deviceId);
        device.setStatus(DeviceStatus.SUSPENDED);
        return deviceMapper.toResponse(deviceRepository.save(device));
    }

    @Override
    @Transactional
    public DeviceResponse activate(UUID tenantId, UUID deviceId) {
        Device device = findOrThrow(tenantId, deviceId);
        device.setStatus(DeviceStatus.ACTIVE);
        return deviceMapper.toResponse(deviceRepository.save(device));
    }

    @Override
    @Transactional
    public void deregister(UUID tenantId, UUID deviceId) {
        Device device = findOrThrow(tenantId, deviceId);
        device.setActive(false);
        device.setStatus(DeviceStatus.INACTIVE);
        deviceRepository.save(device);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceResponse> getAll(UUID tenantId) {
        return deviceRepository.findAllByTenantIdAndActiveTrue(tenantId)
                .stream().map(deviceMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceResponse getById(UUID tenantId, UUID deviceId) {
        return deviceMapper.toResponse(findOrThrow(tenantId, deviceId));
    }

    private Device findOrThrow(UUID tenantId, UUID deviceId) {
        return deviceRepository.findByIdAndTenantIdAndActiveTrue(deviceId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Device", deviceId));
    }
}
