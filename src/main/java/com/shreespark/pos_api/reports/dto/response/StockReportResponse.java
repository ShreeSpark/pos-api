package com.shreespark.pos_api.reports.dto.response;

import java.util.List;

public record StockReportResponse(
        int totalProducts,
        int lowStockCount,
        int outOfStockCount,
        List<StockItem> items
) {
    public record StockItem(
            String productId,
            String productName,
            String sku,
            String categoryName,
            int currentStock,
            int lowStockThreshold,
            boolean isLowStock,
            boolean isOutOfStock
    ) {}
}
