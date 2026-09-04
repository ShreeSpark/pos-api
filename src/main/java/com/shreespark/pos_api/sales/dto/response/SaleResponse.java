package com.shreespark.pos_api.sales.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SaleResponse(
        UUID id,
        String invoiceNumber,
        UUID customerId,
        String customerName,
        String status,
        String paymentMethod,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal taxableAmount,
        BigDecimal cgstAmount,
        BigDecimal sgstAmount,
        BigDecimal igstAmount,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal khataAmount,
        boolean interState,
        String note,
        List<SaleItemResponse> items,
        Instant createdAt
) {
    public record SaleItemResponse(
            UUID id,
            UUID productId,
            String productName,
            String hsnCode,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal discountPercent,
            BigDecimal discountAmount,
            BigDecimal taxableAmount,
            BigDecimal cgstPercent,
            BigDecimal cgstAmount,
            BigDecimal sgstPercent,
            BigDecimal sgstAmount,
            BigDecimal igstPercent,
            BigDecimal igstAmount,
            BigDecimal lineTotal
    ) {}
}
