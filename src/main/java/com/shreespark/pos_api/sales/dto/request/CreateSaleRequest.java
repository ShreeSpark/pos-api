package com.shreespark.pos_api.sales.dto.request;

import com.shreespark.pos_api.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateSaleRequest(
        UUID customerId,
        @NotNull PaymentMethod paymentMethod,
        @NotEmpty List<SaleItemRequest> items,
        // for split payment
        BigDecimal cashAmount,
        BigDecimal upiAmount,
        BigDecimal cardAmount,
        // inter-state sale flag (IGST instead of CGST+SGST)
        boolean interState,
        String note
) {}
