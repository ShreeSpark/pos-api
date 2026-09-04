package com.shreespark.pos_api.membership.dto.request;

import com.shreespark.pos_api.common.enums.MembershipTier;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateMembershipRequest(
        @NotNull MembershipTier tier,
        @NotBlank String name,
        @NotNull @DecimalMin("0.0") BigDecimal discountPercent,
        BigDecimal minPurchaseAmount,
        @NotNull @Min(1) Integer validityDays,
        String description
) {}
