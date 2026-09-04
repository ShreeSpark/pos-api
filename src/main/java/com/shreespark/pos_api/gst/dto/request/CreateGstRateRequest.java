package com.shreespark.pos_api.gst.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateGstRateRequest(
        @NotBlank String name,
        @NotNull @DecimalMin("0.0") BigDecimal rate,
        String description
) {}
