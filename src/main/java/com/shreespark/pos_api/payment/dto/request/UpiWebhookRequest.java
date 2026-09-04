package com.shreespark.pos_api.payment.dto.request;

import java.math.BigDecimal;

public record UpiWebhookRequest(
        String transactionId,
        String referenceNumber,
        BigDecimal amount,
        String status,
        String invoiceNumber
) {}
