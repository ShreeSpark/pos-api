package com.shreespark.pos_api.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StockAdjustmentRequest(
        @NotNull UUID productId,
        @NotNull Integer adjustedQuantity,
        @NotBlank String reason
) {}
