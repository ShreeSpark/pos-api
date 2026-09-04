package com.shreespark.pos_api.brand.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateBrandRequest(
        @NotBlank String name,
        String description
) {}
