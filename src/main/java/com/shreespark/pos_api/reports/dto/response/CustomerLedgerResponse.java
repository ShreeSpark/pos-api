package com.shreespark.pos_api.reports.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CustomerLedgerResponse(
        String customerId,
        String customerName,
        String phone,
        BigDecimal creditLimit,
        BigDecimal outstandingBalance,
        List<LedgerEntry> entries
) {
    public record LedgerEntry(
            String entryId,
            String type,
            BigDecimal amount,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter,
            String note,
            String createdAt
    ) {}
}
