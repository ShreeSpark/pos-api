package com.shreespark.pos_api.payment.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.payment.dto.request.UpiWebhookRequest;
import com.shreespark.pos_api.payment.dto.response.PaymentTransactionResponse;
import com.shreespark.pos_api.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/sales/{saleId}/payments")
    @PreAuthorize("hasAuthority('BILLING_VIEW_ALL')")
    public ResponseEntity<ApiResponse<List<PaymentTransactionResponse>>> getBySale(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID saleId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getBySale(tenantId, saleId)));
    }

    @GetMapping("/customers/{customerId}/payments")
    @PreAuthorize("hasAuthority('BILLING_VIEW_ALL')")
    public ResponseEntity<ApiResponse<List<PaymentTransactionResponse>>> getByCustomer(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getByCustomer(tenantId, customerId)));
    }

    // UPI payment gateway webhook — public endpoint, no auth
    @PostMapping("/payments/upi/webhook")
    public ResponseEntity<Void> upiWebhook(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody UpiWebhookRequest request) {
        paymentService.handleUpiWebhook(tenantId, request);
        return ResponseEntity.ok().build();
    }
}
