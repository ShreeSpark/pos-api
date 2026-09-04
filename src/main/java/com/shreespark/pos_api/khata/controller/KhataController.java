package com.shreespark.pos_api.khata.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.khata.dto.request.ManualKhataEntryRequest;
import com.shreespark.pos_api.khata.dto.request.RecordPaymentRequest;
import com.shreespark.pos_api.khata.dto.response.KhataEntryResponse;
import com.shreespark.pos_api.khata.service.KhataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers/{customerId}/khata")
@RequiredArgsConstructor
public class KhataController {

    private final KhataService khataService;

    @GetMapping
    @PreAuthorize("hasAuthority('KHATA_VIEW')")
    public ResponseEntity<ApiResponse<List<KhataEntryResponse>>> getByCustomer(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.ok(khataService.getByCustomer(tenantId, customerId)));
    }

    @PostMapping("/entries")
    @PreAuthorize("hasAuthority('KHATA_MANUAL_ENTRY')")
    public ResponseEntity<ApiResponse<KhataEntryResponse>> manualEntry(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID customerId,
            @Valid @RequestBody ManualKhataEntryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Entry recorded",
                khataService.manualEntry(tenantId, customerId, request)));
    }

    @PostMapping("/payments")
    @PreAuthorize("hasAuthority('KHATA_RECORD_PAYMENT')")
    public ResponseEntity<ApiResponse<KhataEntryResponse>> recordPayment(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID customerId,
            @Valid @RequestBody RecordPaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Payment recorded",
                khataService.recordPayment(tenantId, customerId, request)));
    }
}
