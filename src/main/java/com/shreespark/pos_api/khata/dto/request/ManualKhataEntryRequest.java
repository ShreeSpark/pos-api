package com.shreespark.pos_api.khata.dto.request;

import com.shreespark.pos_api.common.enums.KhataEntryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ManualKhataEntryRequest(
        @NotNull KhataEntryType type,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String note
) {}
