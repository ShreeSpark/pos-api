package com.shreespark.pos_api.barcode.dto.response;

import java.util.UUID;

public record BarcodeResponse(
        UUID id,
        UUID productId,
        String value,
        String format,
        String imageBase64
) {}
