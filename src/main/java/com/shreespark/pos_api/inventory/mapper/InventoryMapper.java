package com.shreespark.pos_api.inventory.mapper;

import com.shreespark.pos_api.inventory.dto.response.StockAdjustmentResponse;
import com.shreespark.pos_api.inventory.dto.response.StockMovementResponse;
import com.shreespark.pos_api.inventory.dto.response.StockSummaryResponse;
import com.shreespark.pos_api.inventory.entity.StockAdjustment;
import com.shreespark.pos_api.inventory.entity.StockLedger;
import com.shreespark.pos_api.inventory.entity.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public StockMovementResponse toMovementResponse(StockMovement m) {
        return new StockMovementResponse(
                m.getId(),
                m.getProduct().getId(),
                m.getProduct().getName(),
                m.getType().name(),
                m.getQuantity(),
                m.getStockBefore(),
                m.getStockAfter(),
                m.getReferenceId(),
                m.getNote(),
                m.getCreatedAt()
        );
    }

    public StockAdjustmentResponse toAdjustmentResponse(StockAdjustment a) {
        return new StockAdjustmentResponse(
                a.getId(),
                a.getProduct().getId(),
                a.getProduct().getName(),
                a.getAdjustedQuantity(),
                a.getStockBefore(),
                a.getStockAfter(),
                a.getReason(),
                a.getApprovedBy(),
                a.getCreatedAt()
        );
    }

    public StockSummaryResponse toStockSummary(StockLedger ledger) {
        var p = ledger.getProduct();
        return new StockSummaryResponse(
                p.getId(),
                p.getName(),
                p.getSku(),
                ledger.getCurrentStock(),
                p.getLowStockThreshold(),
                p.getMoq(),
                ledger.getCurrentStock() <= p.getLowStockThreshold()
        );
    }
}
