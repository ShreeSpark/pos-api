package com.shreespark.pos_api.barcode.dto.request;

import com.shreespark.pos_api.common.enums.BarcodeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenerateBarcodeRequest(
        @NotBlank String value,
        @NotNull BarcodeFormat format
) {}
