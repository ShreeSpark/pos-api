package com.shreespark.pos_api.inventory.service;

import com.shreespark.pos_api.common.enums.StockMovementType;
import com.shreespark.pos_api.inventory.dto.request.StockAdjustmentRequest;
import com.shreespark.pos_api.inventory.dto.response.StockAdjustmentResponse;
import com.shreespark.pos_api.inventory.dto.response.StockMovementResponse;
import com.shreespark.pos_api.inventory.dto.response.StockSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface InventoryService {

    // called internally by sales, purchases, returns
    void recordMovement(UUID tenantId, UUID productId, StockMovementType type,
                        int quantity, String referenceId, String note);

    // manual adjustment by ADMIN/MANAGER
    StockAdjustmentResponse adjust(UUID tenantId, String approvedBy, StockAdjustmentRequest request);

    List<StockMovementResponse> getMovementsByProduct(UUID tenantId, UUID productId);
    List<StockMovementResponse> getAllMovements(UUID tenantId);
    List<StockAdjustmentResponse> getAdjustmentsByProduct(UUID tenantId, UUID productId);
    List<StockAdjustmentResponse> getAllAdjustments(UUID tenantId);
    List<StockSummaryResponse> getStockSummary(UUID tenantId);
    List<StockSummaryResponse> getLowStock(UUID tenantId);
}
