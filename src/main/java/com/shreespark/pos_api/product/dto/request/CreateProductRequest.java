package com.shreespark.pos_api.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        @NotBlank String name,
        String description,
        String sku,
        @NotNull @DecimalMin("0.0") BigDecimal retailPrice,
        BigDecimal wholesalePrice,
        BigDecimal dealerPrice,
        BigDecimal costPrice,
        @Min(1) Integer lowStockThreshold,
        @Min(1) Integer moq,
        UUID categoryId,
        UUID brandId
) {}
