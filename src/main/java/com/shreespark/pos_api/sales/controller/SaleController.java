package com.shreespark.pos_api.sales.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.sales.dto.request.CreateSaleRequest;
import com.shreespark.pos_api.sales.dto.request.UpdateSaleRequest;
import com.shreespark.pos_api.sales.dto.response.SaleAuditLogResponse;
import com.shreespark.pos_api.sales.dto.response.SaleResponse;
import com.shreespark.pos_api.sales.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasAuthority('BILLING_CREATE')")
    public ResponseEntity<ApiResponse<SaleResponse>> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @AuthenticationPrincipal String staffId,
            @Valid @RequestBody CreateSaleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Sale created",
                        saleService.create(tenantId, UUID.fromString(staffId), request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('BILLING_VIEW_ALL', 'BILLING_CREATE')")
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getAll(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(saleService.getAll(tenantId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('BILLING_VIEW_ALL', 'BILLING_CREATE')")
    public ResponseEntity<ApiResponse<SaleResponse>> getById(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(saleService.getById(tenantId, id)));
    }

    @GetMapping("/invoice/{invoiceNumber}")
    @PreAuthorize("hasAnyAuthority('BILLING_VIEW_ALL', 'BILLING_CREATE')")
    public ResponseEntity<ApiResponse<SaleResponse>> getByInvoice(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable String invoiceNumber) {
        return ResponseEntity.ok(ApiResponse.ok(saleService.getByInvoiceNumber(tenantId, invoiceNumber)));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('BILLING_VIEW_ALL', 'BILLING_CREATE')")
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getByCustomer(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.ok(saleService.getByCustomer(tenantId, customerId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') || hasAuthority('BILLING_EDIT')")
    public ResponseEntity<ApiResponse<SaleResponse>> update(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @AuthenticationPrincipal String staffId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSaleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Sale updated", saleService.update(tenantId, UUID.fromString(staffId), id, request)));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') || hasAuthority('BILLING_CANCEL')")
    public ResponseEntity<ApiResponse<SaleResponse>> cancel(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @AuthenticationPrincipal String staffId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Sale cancelled", saleService.cancel(tenantId, UUID.fromString(staffId), id)));
    }

    @PostMapping("/{id}/print-log")
    @PreAuthorize("hasAnyAuthority('BILLING_VIEW_ALL', 'BILLING_CREATE')")
    public ResponseEntity<ApiResponse<SaleResponse>> logPrint(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @AuthenticationPrincipal String staffId,
            @PathVariable UUID id,
            @RequestParam(defaultValue = "CASH_MEMO") String printType) {
        return ResponseEntity.ok(ApiResponse.ok("Print logged", saleService.logPrint(tenantId, UUID.fromString(staffId), id, printType)));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('BILLING_VIEW_ALL')")
    public ResponseEntity<ApiResponse<List<SaleAuditLogResponse>>> getAuditLogs(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(saleService.getAllAuditLogs(tenantId)));
    }
}
