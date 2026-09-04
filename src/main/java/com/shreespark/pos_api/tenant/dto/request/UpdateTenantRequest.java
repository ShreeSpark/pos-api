package com.shreespark.pos_api.tenant.dto.request;

public record UpdateTenantRequest(
        String businessName,
        String phone,
        String address,
        String gstin
) {}
