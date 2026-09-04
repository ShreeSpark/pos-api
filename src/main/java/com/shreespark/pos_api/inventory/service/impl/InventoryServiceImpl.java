package com.shreespark.pos_api.inventory.service.impl;

import com.shreespark.pos_api.common.enums.StockMovementType;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.inventory.dto.request.StockAdjustmentRequest;
import com.shreespark.pos_api.inventory.dto.response.StockAdjustmentResponse;
import com.shreespark.pos_api.inventory.dto.response.StockMovementResponse;
import com.shreespark.pos_api.inventory.dto.response.StockSummaryResponse;
import com.shreespark.pos_api.inventory.entity.StockAdjustment;
import com.shreespark.pos_api.inventory.entity.StockLedger;
import com.shreespark.pos_api.inventory.entity.StockMovement;
import com.shreespark.pos_api.inventory.mapper.InventoryMapper;
import com.shreespark.pos_api.inventory.repository.StockAdjustmentRepository;
import com.shreespark.pos_api.inventory.repository.StockLedgerRepository;
import com.shreespark.pos_api.inventory.repository.StockMovementRepository;
import com.shreespark.pos_api.inventory.service.InventoryService;
import com.shreespark.pos_api.product.entity.Product;
import com.shreespark.pos_api.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final StockLedgerRepository stockLedgerRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public void recordMovement(UUID tenantId, UUID productId, StockMovementType type,
                               int quantity, String referenceId, String note) {
        Product product = productRepository.findByIdAndTenantIdAndActiveTrue(productId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        StockLedger ledger = stockLedgerRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("StockLedger", productId));

        int stockBefore = ledger.getCurrentStock();
        int stockAfter = switch (type) {
            case PURCHASE, RETURN -> stockBefore + quantity;
            case SALE, DAMAGE     -> stockBefore - quantity;
            case ADJUSTMENT       -> quantity;
        };

        if (stockAfter < 0) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        ledger.setCurrentStock(stockAfter);
        stockLedgerRepository.save(ledger);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .type(type)
                .quantity(quantity)
                .stockBefore(stockBefore)
                .stockAfter(stockAfter)
                .referenceId(referenceId)
                .note(note)
                .build();
        movement.setTenantId(tenantId);
        stockMovementRepository.save(movement);
    }

    @Override
    @Transactional
    public StockAdjustmentResponse adjust(UUID tenantId, String approvedBy, StockAdjustmentRequest request) {
        Product product = productRepository.findByIdAndTenantIdAndActiveTrue(request.productId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.productId()));

        StockLedger ledger = stockLedgerRepository.findByProductId(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("StockLedger", request.productId()));

        int stockBefore = ledger.getCurrentStock();
        int stockAfter  = stockBefore + request.adjustedQuantity();

        if (stockAfter < 0) {
            throw new RuntimeException("Adjustment would result in negative stock");
        }

        ledger.setCurrentStock(stockAfter);
        stockLedgerRepository.save(ledger);

        StockAdjustment adjustment = StockAdjustment.builder()
                .product(product)
                .adjustedQuantity(request.adjustedQuantity())
                .stockBefore(stockBefore)
                .stockAfter(stockAfter)
                .reason(request.reason())
                .approvedBy(approvedBy)
                .build();
        adjustment.setTenantId(tenantId);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .type(StockMovementType.ADJUSTMENT)
                .quantity(Math.abs(request.adjustedQuantity()))
                .stockBefore(stockBefore)
                .stockAfter(stockAfter)
                .note(request.reason())
                .build();
        movement.setTenantId(tenantId);
        stockMovementRepository.save(movement);

        return inventoryMapper.toAdjustmentResponse(stockAdjustmentRepository.save(adjustment));
    }

    @Override
    public List<StockMovementResponse> getMovementsByProduct(UUID tenantId, UUID productId) {
        return stockMovementRepository
                .findAllByProductIdAndProductTenantIdOrderByCreatedAtDesc(productId, tenantId)
                .stream().map(inventoryMapper::toMovementResponse).toList();
    }

    @Override
    public List<StockMovementResponse> getAllMovements(UUID tenantId) {
        return stockMovementRepository
                .findAllByProductTenantIdOrderByCreatedAtDesc(tenantId)
                .stream().map(inventoryMapper::toMovementResponse).toList();
    }

    @Override
    public List<StockAdjustmentResponse> getAdjustmentsByProduct(UUID tenantId, UUID productId) {
        return stockAdjustmentRepository
                .findAllByProductIdAndProductTenantIdOrderByCreatedAtDesc(productId, tenantId)
                .stream().map(inventoryMapper::toAdjustmentResponse).toList();
    }

    @Override
    public List<StockAdjustmentResponse> getAllAdjustments(UUID tenantId) {
        return stockAdjustmentRepository
                .findAllByProductTenantIdOrderByCreatedAtDesc(tenantId)
                .stream().map(inventoryMapper::toAdjustmentResponse).toList();
    }

    @Override
    public List<StockSummaryResponse> getStockSummary(UUID tenantId) {
        return stockLedgerRepository.findAllByTenantId(tenantId)
                .stream().map(inventoryMapper::toStockSummary).toList();
    }

    @Override
    public List<StockSummaryResponse> getLowStock(UUID tenantId) {
        return stockLedgerRepository.findLowStockByTenantId(tenantId)
                .stream().map(inventoryMapper::toStockSummary).toList();
    }
}
