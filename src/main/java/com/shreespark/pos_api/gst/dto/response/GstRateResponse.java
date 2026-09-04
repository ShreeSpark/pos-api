package com.shreespark.pos_api.gst.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record GstRateResponse(
        UUID id,
        String name,
        BigDecimal rate,
        BigDecimal cgstRate,
        BigDecimal sgstRate,
        BigDecimal igstRate,
        String description
) {}
