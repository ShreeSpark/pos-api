package com.shreespark.pos_api.sales.dto.request;

import java.math.BigDecimal;

public record UpdateSaleRequest(
        String paymentMethod,
        String status,
        String note,
        Boolean interState,
        BigDecimal cashAmount,
        BigDecimal upiAmount,
        BigDecimal cardAmount,
        String editReason
) {}
