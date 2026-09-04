package com.shreespark.pos_api.sync.repository;

import com.shreespark.pos_api.common.enums.SyncStatus;
import com.shreespark.pos_api.sync.entity.SyncQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SyncQueueRepository extends JpaRepository<SyncQueue, UUID> {
    Optional<SyncQueue> findByClientRequestIdAndTenantId(String clientRequestId, UUID tenantId);
    List<SyncQueue> findAllByDeviceIdAndTenantIdOrderBySequenceAsc(UUID deviceId, UUID tenantId);
    List<SyncQueue> findAllByTenantIdAndStatusOrderBySequenceAsc(UUID tenantId, SyncStatus status);
}
