package com.shreespark.pos_api.device.service.impl;

import com.shreespark.pos_api.common.enums.DeviceStatus;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.device.dto.request.RegisterDeviceRequest;
import com.shreespark.pos_api.device.dto.response.DeviceResponse;
import com.shreespark.pos_api.device.entity.Device;
import com.shreespark.pos_api.device.mapper.DeviceMapper;
import com.shreespark.pos_api.device.repository.DeviceRepository;
import com.shreespark.pos_api.device.service.DeviceService;
import com.shreespark.pos_api.subscription.service.PlanLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;
    private final PlanLimitService planLimitService;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHANUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    @Override
    @Transactional
    public DeviceResponse register(UUID tenantId, String registeredBy, RegisterDeviceRequest req) {
        var existingOpt = deviceRepository.findByDeviceCodeAndTenantId(req.deviceCode(), tenantId);
        if (existingOpt.isPresent()) {
            Device existing = existingOpt.get();
            if (existing.getStatus() == DeviceStatus.SUSPENDED || existing.getStatus() == DeviceStatus.TERMINATED) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Device license access is revoked or suspended by platform admin");
            }
            existing.setLastSeenAt(Instant.now());
            if (req.deviceName() != null) existing.setDeviceName(req.deviceName());
            if (req.platform() != null) existing.setPlatform(req.platform());
            if (req.appVersion() != null) existing.setAppVersion(req.appVersion());
            return deviceMapper.toResponse(deviceRepository.save(existing));
        }

        planLimitService.checkLimit(tenantId, PlanLimitService.LimitType.DEVICES,
                deviceRepository.countByTenantIdAndActiveTrueAndStatus(tenantId, DeviceStatus.ACTIVE));

        Device device = Device.builder()
                .deviceCode(req.deviceCode())
                .deviceName(req.deviceName())
                .platform(req.platform())
                .appVersion(req.appVersion())
                .status(DeviceStatus.PENDING)
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
        if (device.getStatus() == DeviceStatus.SUSPENDED || device.getStatus() == DeviceStatus.TERMINATED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Device license status: " + device.getStatus());
        }
        device.setLastSeenAt(Instant.now());
        return deviceMapper.toResponse(deviceRepository.save(device));
    }

    @Override
    @Transactional
    public DeviceResponse approve(UUID tenantId, UUID deviceId) {
        Device device = findOrThrow(tenantId, deviceId);
        device.setStatus(DeviceStatus.ACTIVE);
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
    public DeviceResponse terminate(UUID tenantId, UUID deviceId) {
        Device device = findOrThrow(tenantId, deviceId);
        device.setStatus(DeviceStatus.TERMINATED);
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
    public String generateProductKey(UUID tenantId, UUID deviceId, Integer validDays, String customExpiryDate) {
        Device device = findOrThrow(tenantId, deviceId);
        String key = generateKeyString();
        device.setProductKey(key);
        device.setStatus(DeviceStatus.ACTIVE);

        if (customExpiryDate != null && !customExpiryDate.isBlank()) {
            try {
                if (customExpiryDate.contains("T")) {
                    device.setExpiresAt(Instant.parse(customExpiryDate));
                } else {
                    device.setExpiresAt(java.time.LocalDate.parse(customExpiryDate).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                }
            } catch (Exception e) {
                device.setExpiresAt(null);
            }
        } else if (validDays != null && validDays > 0) {
            device.setExpiresAt(Instant.now().plus(validDays, java.time.temporal.ChronoUnit.DAYS));
        } else {
            device.setExpiresAt(null); // Lifetime key
        }

        deviceRepository.save(device);
        return key;
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
    @Transactional
    public void deleteDevice(UUID tenantId, UUID deviceId) {
        Device device = findOrThrow(tenantId, deviceId);
        deviceRepository.delete(device);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceResponse> getAll(UUID tenantId) {
        return deviceRepository.findAllByTenantIdAndActiveTrue(tenantId)
                .stream().map(deviceMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceResponse> getPending(UUID tenantId) {
        return deviceRepository.findAllByTenantIdAndActiveTrueAndStatus(tenantId, DeviceStatus.PENDING)
                .stream().map(deviceMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceResponse getById(UUID tenantId, UUID deviceId) {
        return deviceMapper.toResponse(findOrThrow(tenantId, deviceId));
    }

    @Override
    @Transactional
    public DeviceResponse publicRegister(RegisterDeviceRequest req) {
        var existingOpt = deviceRepository.findByDeviceCode(req.deviceCode());
        if (existingOpt.isPresent()) {
            Device existing = existingOpt.get();
            existing.setLastSeenAt(Instant.now());
            if (req.deviceName() != null) existing.setDeviceName(req.deviceName());
            if (req.platform() != null) existing.setPlatform(req.platform());
            if (req.appVersion() != null) existing.setAppVersion(req.appVersion());
            return deviceMapper.toResponse(deviceRepository.save(existing));
        }

        Device device = Device.builder()
                .deviceCode(req.deviceCode())
                .deviceName(req.deviceName() != null ? req.deviceName() : "POS Standalone Device")
                .platform(req.platform() != null ? req.platform() : "Windows")
                .appVersion(req.appVersion() != null ? req.appVersion() : "1.0.0")
                .status(DeviceStatus.PENDING)
                .registeredBy("AUTO_INSTALL")
                .lastSeenAt(Instant.now())
                .build();
        return deviceMapper.toResponse(deviceRepository.save(device));
    }

    @Override
    @Transactional
    public DeviceResponse publicHeartbeat(String deviceCode) {
        Device device = deviceRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device", deviceCode));

        device.setLastSeenAt(Instant.now());

        if (device.getExpiresAt() != null && Instant.now().isAfter(device.getExpiresAt())) {
            device.setStatus(DeviceStatus.TERMINATED);
            deviceRepository.save(device);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "LICENSE EXPIRED. Product Activation Key expired on " + device.getExpiresAt());
        }

        if (device.getStatus() == DeviceStatus.SUSPENDED || device.getStatus() == DeviceStatus.TERMINATED) {
            deviceRepository.save(device);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "LICENSE REVOKED / TERMINATED BY VENDOR. Status: " + device.getStatus());
        }

        deviceRepository.save(device);
        return deviceMapper.toResponse(device);
    }

    @Override
    @Transactional
    public DeviceResponse verifyKey(String deviceCode, String productKey) {
        Device device = deviceRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device", deviceCode));

        if (device.getExpiresAt() != null && Instant.now().isAfter(device.getExpiresAt())) {
            device.setStatus(DeviceStatus.TERMINATED);
            deviceRepository.save(device);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Product Key has expired on " + device.getExpiresAt());
        }

        if (productKey != null) {
            String cleanKey = productKey.trim().toUpperCase();
            if (cleanKey.startsWith("SPARK-") || (device.getProductKey() != null && device.getProductKey().equalsIgnoreCase(cleanKey))) {
                device.setProductKey(cleanKey);
                device.setStatus(DeviceStatus.ACTIVE);
                return deviceMapper.toResponse(deviceRepository.save(device));
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Activation Key format");
    }

    private Device findOrThrow(UUID tenantId, UUID deviceId) {
        if (tenantId != null && !tenantId.toString().equals("00000000-0000-0000-0000-000000000000")) {
            var found = deviceRepository.findByIdAndTenantIdAndActiveTrue(deviceId, tenantId);
            if (found.isPresent()) {
                return found.get();
            }
        }
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device", deviceId));
    }

    private String generateKeyString() {
        return "SPARK-" + randomBlock(4) + "-" + randomBlock(4) + "-" + randomBlock(4) + "-" + randomBlock(4);
    }

    private String randomBlock(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}
