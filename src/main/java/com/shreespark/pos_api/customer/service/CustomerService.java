package com.shreespark.pos_api.customer.service;

import com.shreespark.pos_api.customer.dto.request.CreateCustomerRequest;
import com.shreespark.pos_api.customer.dto.request.UpdateCustomerRequest;
import com.shreespark.pos_api.customer.dto.response.CustomerResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse create(UUID tenantId, CreateCustomerRequest request);
    CustomerResponse getById(UUID tenantId, UUID id);
    CustomerResponse getByPhone(UUID tenantId, String phone);
    List<CustomerResponse> getAll(UUID tenantId);
    CustomerResponse update(UUID tenantId, UUID id, UpdateCustomerRequest request);
    void delete(UUID tenantId, UUID id);

    // called internally by sales/payment modules
    void incrementOutstanding(UUID tenantId, UUID customerId, BigDecimal amount);
    void decrementOutstanding(UUID tenantId, UUID customerId, BigDecimal amount);
}
