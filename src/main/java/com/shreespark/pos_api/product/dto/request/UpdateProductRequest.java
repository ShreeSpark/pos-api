package com.shreespark.pos_api.product.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
        String name,
        String description,
        String sku,
        BigDecimal retailPrice,
        BigDecimal wholesalePrice,
        BigDecimal dealerPrice,
        BigDecimal costPrice,
        Integer lowStockThreshold,
        Integer moq,
        UUID categoryId,
        UUID brandId
) {}
