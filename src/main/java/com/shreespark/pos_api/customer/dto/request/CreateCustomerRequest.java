package com.shreespark.pos_api.customer.dto.request;

import com.shreespark.pos_api.common.enums.CustomerType;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateCustomerRequest(
        @NotBlank String name,
        @NotBlank String phone,
        String email,
        String address,
        String gstin,
        CustomerType type,
        BigDecimal creditLimit
) {}
