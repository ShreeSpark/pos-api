package com.shreespark.pos_api.sales.repository;

import com.shreespark.pos_api.sales.entity.SaleAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SaleAuditLogRepository extends JpaRepository<SaleAuditLog, UUID> {
    List<SaleAuditLog> findBySaleIdAndTenantIdOrderByCreatedAtDesc(UUID saleId, UUID tenantId);
    List<SaleAuditLog> findAllByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}
