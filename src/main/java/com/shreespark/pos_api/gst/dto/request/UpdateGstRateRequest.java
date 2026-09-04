package com.shreespark.pos_api.gst.dto.request;

import java.math.BigDecimal;

public record UpdateGstRateRequest(
        String name,
        BigDecimal rate,
        String description
) {}
