package com.shreespark.pos_api.customer.repository;

import com.shreespark.pos_api.common.enums.CustomerType;
import com.shreespark.pos_api.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByIdAndTenantIdAndActiveTrue(UUID id, UUID tenantId);
    List<Customer> findAllByTenantIdAndActiveTrue(UUID tenantId);
    List<Customer> findAllByTenantIdAndTypeAndActiveTrue(UUID tenantId, CustomerType type);
    Optional<Customer> findByPhoneAndTenantId(String phone, UUID tenantId);
    boolean existsByPhoneAndTenantId(String phone, UUID tenantId);
}
