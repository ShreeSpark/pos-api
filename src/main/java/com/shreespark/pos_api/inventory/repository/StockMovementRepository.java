package com.shreespark.pos_api.inventory.repository;

import com.shreespark.pos_api.common.enums.StockMovementType;
import com.shreespark.pos_api.inventory.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    List<StockMovement> findAllByProductIdAndProductTenantIdOrderByCreatedAtDesc(UUID productId, UUID tenantId);
    List<StockMovement> findAllByProductTenantIdAndTypeOrderByCreatedAtDesc(UUID tenantId, StockMovementType type);
    List<StockMovement> findAllByProductTenantIdOrderByCreatedAtDesc(UUID tenantId);
}
