package com.shreespark.pos_api.payment.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentTransactionResponse(
        UUID id,
        UUID saleId,
        UUID customerId,
        String method,
        String status,
        BigDecimal amount,
        String referenceNumber,
        String note,
        Instant createdAt
) {}
