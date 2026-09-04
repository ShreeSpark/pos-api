package com.shreespark.pos_api.khata.service.impl;

import com.shreespark.pos_api.common.enums.KhataEntryType;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.customer.entity.Customer;
import com.shreespark.pos_api.customer.repository.CustomerRepository;
import com.shreespark.pos_api.khata.dto.request.ManualKhataEntryRequest;
import com.shreespark.pos_api.khata.dto.request.RecordPaymentRequest;
import com.shreespark.pos_api.khata.dto.response.KhataEntryResponse;
import com.shreespark.pos_api.khata.entity.KhataEntry;
import com.shreespark.pos_api.khata.mapper.KhataMapper;
import com.shreespark.pos_api.khata.repository.KhataEntryRepository;
import com.shreespark.pos_api.khata.service.KhataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KhataServiceImpl implements KhataService {

    private final KhataEntryRepository khataEntryRepository;
    private final CustomerRepository customerRepository;
    private final KhataMapper khataMapper;

    @Override
    @Transactional
    public KhataEntry recordEntry(UUID tenantId, UUID customerId, KhataEntryType type,
                                  BigDecimal amount, UUID referenceId, String note) {
        Customer customer = findCustomerOrThrow(tenantId, customerId);

        BigDecimal balanceBefore = customer.getOutstandingBalance();
        BigDecimal balanceAfter = type == KhataEntryType.DEBIT
                ? balanceBefore.add(amount)
                : balanceBefore.subtract(amount);

        if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) balanceAfter = BigDecimal.ZERO;

        customer.setOutstandingBalance(balanceAfter);
        customerRepository.save(customer);

        KhataEntry entry = KhataEntry.builder()
                .customer(customer)
                .type(type)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .referenceId(referenceId)
                .note(note)
                .build();
        entry.setTenantId(tenantId);
        return khataEntryRepository.save(entry);
    }

    @Override
    @Transactional
    public KhataEntryResponse manualEntry(UUID tenantId, UUID customerId,
                                          ManualKhataEntryRequest request) {
        return khataMapper.toResponse(
                recordEntry(tenantId, customerId, request.type(),
                        request.amount(), null, request.note()));
    }

    @Override
    @Transactional
    public KhataEntryResponse recordPayment(UUID tenantId, UUID customerId,
                                            RecordPaymentRequest request) {
        Customer customer = findCustomerOrThrow(tenantId, customerId);
        if (customer.getOutstandingBalance().compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("No outstanding balance for customer");
        }
        return khataMapper.toResponse(
                recordEntry(tenantId, customerId, KhataEntryType.CREDIT,
                        request.amount(), null, request.note()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhataEntryResponse> getByCustomer(UUID tenantId, UUID customerId) {
        return khataEntryRepository
                .findAllByCustomerIdAndCustomerTenantIdOrderByCreatedAtDesc(customerId, tenantId)
                .stream().map(khataMapper::toResponse).toList();
    }

    private Customer findCustomerOrThrow(UUID tenantId, UUID customerId) {
        return customerRepository.findByIdAndTenantIdAndActiveTrue(customerId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
    }
}
