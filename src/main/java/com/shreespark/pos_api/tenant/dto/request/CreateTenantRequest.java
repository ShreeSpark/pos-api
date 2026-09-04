package com.shreespark.pos_api.tenant.dto.request;

import com.shreespark.pos_api.common.enums.SubscriptionPlan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateTenantRequest(
        @NotBlank String businessName,
        @Email @NotBlank String email,
        @NotBlank String phone,
        String address,
        String gstin,
        @NotNull SubscriptionPlan subscriptionPlan,
        @NotNull LocalDate subscriptionExpiry,
        @NotBlank String adminName,
        @NotBlank String adminPassword
) {}
