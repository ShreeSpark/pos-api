package com.shreespark.pos_api.inventory.repository;

import com.shreespark.pos_api.inventory.entity.StockLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockLedgerRepository extends JpaRepository<StockLedger, UUID> {

    Optional<StockLedger> findByProductId(UUID productId);

    @Query("SELECT sl FROM StockLedger sl WHERE sl.product.tenantId = :tenantId")
    List<StockLedger> findAllByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT sl FROM StockLedger sl WHERE sl.product.tenantId = :tenantId " +
           "AND sl.currentStock <= sl.product.lowStockThreshold")
    List<StockLedger> findLowStockByTenantId(@Param("tenantId") UUID tenantId);
}
