package com.shreespark.pos_api.customer.dto.request;

import com.shreespark.pos_api.common.enums.CustomerType;

import java.math.BigDecimal;

public record UpdateCustomerRequest(
        String name,
        String phone,
        String email,
        String address,
        String gstin,
        CustomerType type,
        BigDecimal creditLimit
) {}
