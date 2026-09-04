package com.shreespark.pos_api.category.dto.request;

import java.util.UUID;

public record UpdateCategoryRequest(
        String name,
        String description,
        String hsnCode,
        UUID gstRateId
) {}
