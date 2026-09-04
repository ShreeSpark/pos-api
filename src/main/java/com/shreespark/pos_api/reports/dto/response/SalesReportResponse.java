package com.shreespark.pos_api.reports.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record SalesReportResponse(
        int totalSales,
        int cancelledSales,
        BigDecimal totalRevenue,
        BigDecimal totalDiscount,
        BigDecimal totalCgst,
        BigDecimal totalSgst,
        BigDecimal totalIgst,
        BigDecimal totalTax,
        BigDecimal netRevenue,
        List<DailySalesSummary> dailyBreakdown
) {
    public record DailySalesSummary(
            String date,
            int salesCount,
            BigDecimal revenue,
            BigDecimal tax
    ) {}
}
