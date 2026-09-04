package com.shreespark.pos_api.device.repository;

import com.shreespark.pos_api.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    List<Device> findAllByTenantIdAndActiveTrue(UUID tenantId);
    Optional<Device> findByIdAndTenantIdAndActiveTrue(UUID id, UUID tenantId);
    Optional<Device> findByDeviceCodeAndTenantId(String deviceCode, UUID tenantId);
    boolean existsByDeviceCodeAndTenantId(String deviceCode, UUID tenantId);
    long countByTenantIdAndActiveTrue(UUID tenantId);
}
