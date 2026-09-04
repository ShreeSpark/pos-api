package com.shreespark.pos_api.khata.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RecordPaymentRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String note
) {}
