package com.shreespark.pos_api.tenant.mapper;

import com.shreespark.pos_api.tenant.dto.response.TenantResponse;
import com.shreespark.pos_api.tenant.entity.Tenant;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper {

    public TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getBusinessName(),
                tenant.getEmail(),
                tenant.getPhone(),
                tenant.getAddress(),
                tenant.getGstin(),
                tenant.getStatus().name(),
                tenant.getSubscriptionPlan().name(),
                tenant.getSubscriptionExpiry(),
                tenant.getCreatedAt()
        );
    }
}
