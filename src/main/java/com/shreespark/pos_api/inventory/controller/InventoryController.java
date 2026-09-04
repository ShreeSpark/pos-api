package com.shreespark.pos_api.inventory.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.inventory.dto.request.StockAdjustmentRequest;
import com.shreespark.pos_api.inventory.dto.response.StockAdjustmentResponse;
import com.shreespark.pos_api.inventory.dto.response.StockMovementResponse;
import com.shreespark.pos_api.inventory.dto.response.StockSummaryResponse;
import com.shreespark.pos_api.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/stock")
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<List<StockSummaryResponse>>> getStockSummary(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getStockSummary(tenantId)));
    }

    @GetMapping("/stock/low")
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<List<StockSummaryResponse>>> getLowStock(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getLowStock(tenantId)));
    }

    @GetMapping("/movements")
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getAllMovements(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getAllMovements(tenantId)));
    }

    @GetMapping("/movements/product/{productId}")
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getMovementsByProduct(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getMovementsByProduct(tenantId, productId)));
    }

    @PostMapping("/adjustments")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<StockAdjustmentResponse>> adjust(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @AuthenticationPrincipal String staffId,
            @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Stock adjusted",
                inventoryService.adjust(tenantId, staffId, request)));
    }

    @GetMapping("/adjustments")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<StockAdjustmentResponse>>> getAllAdjustments(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getAllAdjustments(tenantId)));
    }

    @GetMapping("/adjustments/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<StockAdjustmentResponse>>> getAdjustmentsByProduct(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getAdjustmentsByProduct(tenantId, productId)));
    }
}
