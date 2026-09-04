package com.shreespark.pos_api.reports.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record GstReportResponse(
        BigDecimal totalTaxableAmount,
        BigDecimal totalCgst,
        BigDecimal totalSgst,
        BigDecimal totalIgst,
        BigDecimal totalTax,
        List<GstSlabSummary> bySlabs
) {
    public record GstSlabSummary(
            String hsnCode,
            String gstRateName,
            BigDecimal rate,
            BigDecimal taxableAmount,
            BigDecimal cgstAmount,
            BigDecimal sgstAmount,
            BigDecimal igstAmount
    ) {}
}
