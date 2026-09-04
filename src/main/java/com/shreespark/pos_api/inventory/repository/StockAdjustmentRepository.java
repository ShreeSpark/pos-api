package com.shreespark.pos_api.inventory.repository;

import com.shreespark.pos_api.inventory.entity.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, UUID> {
    List<StockAdjustment> findAllByProductIdAndProductTenantIdOrderByCreatedAtDesc(UUID productId, UUID tenantId);
    List<StockAdjustment> findAllByProductTenantIdOrderByCreatedAtDesc(UUID tenantId);
}
