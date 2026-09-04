package com.shreespark.pos_api.customer.service.impl;

import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.customer.dto.request.CreateCustomerRequest;
import com.shreespark.pos_api.customer.dto.request.UpdateCustomerRequest;
import com.shreespark.pos_api.customer.dto.response.CustomerResponse;
import com.shreespark.pos_api.customer.entity.Customer;
import com.shreespark.pos_api.customer.mapper.CustomerMapper;
import com.shreespark.pos_api.customer.repository.CustomerRepository;
import com.shreespark.pos_api.customer.service.CustomerService;
import com.shreespark.pos_api.common.enums.CustomerType;
import com.shreespark.pos_api.membership.repository.MembershipSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final MembershipSubscriptionRepository subscriptionRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse create(UUID tenantId, CreateCustomerRequest request) {
        if (customerRepository.existsByPhoneAndTenantId(request.phone(), tenantId)) {
            throw new RuntimeException("Customer with phone already exists: " + request.phone());
        }
        Customer customer = Customer.builder()
                .name(request.name())
                .phone(request.phone())
                .email(request.email())
                .address(request.address())
                .gstin(request.gstin())
                .type(request.type() != null ? request.type() : CustomerType.RETAIL)
                .creditLimit(request.creditLimit() != null ? request.creditLimit() : BigDecimal.ZERO)
                .outstandingBalance(BigDecimal.ZERO)
                .build();
        customer.setTenantId(tenantId);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponse(saved,
                subscriptionRepository.findActiveByCustomerId(saved.getId(), LocalDate.now()));
    }

    @Override
    public CustomerResponse getById(UUID tenantId, UUID id) {
        Customer customer = findOrThrow(tenantId, id);
        return customerMapper.toResponse(customer,
                subscriptionRepository.findActiveByCustomerId(id, LocalDate.now()));
    }

    @Override
    public CustomerResponse getByPhone(UUID tenantId, String phone) {
        Customer customer = customerRepository.findByPhoneAndTenantId(phone, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", phone));
        return customerMapper.toResponse(customer,
                subscriptionRepository.findActiveByCustomerId(customer.getId(), LocalDate.now()));
    }

    @Override
    public List<CustomerResponse> getAll(UUID tenantId) {
        return customerRepository.findAllByTenantIdAndActiveTrue(tenantId).stream()
                .map(c -> customerMapper.toResponse(c,
                        subscriptionRepository.findActiveByCustomerId(c.getId(), LocalDate.now())))
                .toList();
    }

    @Override
    @Transactional
    public CustomerResponse update(UUID tenantId, UUID id, UpdateCustomerRequest request) {
        Customer customer = findOrThrow(tenantId, id);
        if (request.name() != null) customer.setName(request.name());
        if (request.phone() != null) customer.setPhone(request.phone());
        if (request.email() != null) customer.setEmail(request.email());
        if (request.address() != null) customer.setAddress(request.address());
        if (request.gstin() != null) customer.setGstin(request.gstin());
        if (request.type() != null) customer.setType(request.type());
        if (request.creditLimit() != null) customer.setCreditLimit(request.creditLimit());
        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponse(saved,
                subscriptionRepository.findActiveByCustomerId(id, LocalDate.now()));
    }

    @Override
    @Transactional
    public void delete(UUID tenantId, UUID id) {
        Customer customer = findOrThrow(tenantId, id);
        customer.setActive(false);
        customer.setDeletedAt(Instant.now());
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void incrementOutstanding(UUID tenantId, UUID customerId, BigDecimal amount) {
        Customer customer = findOrThrow(tenantId, customerId);
        customer.setOutstandingBalance(customer.getOutstandingBalance().add(amount));
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void decrementOutstanding(UUID tenantId, UUID customerId, BigDecimal amount) {
        Customer customer = findOrThrow(tenantId, customerId);
        BigDecimal newBalance = customer.getOutstandingBalance().subtract(amount);
        customer.setOutstandingBalance(newBalance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newBalance);
        customerRepository.save(customer);
    }

    private Customer findOrThrow(UUID tenantId, UUID id) {
        return customerRepository.findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }
}
