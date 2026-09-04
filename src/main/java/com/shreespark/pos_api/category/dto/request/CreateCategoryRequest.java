package com.shreespark.pos_api.category.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateCategoryRequest(
        @NotBlank String name,
        String description,
        String hsnCode,
        UUID gstRateId
) {}
