package com.shreespark.pos_api.payment.repository;

import com.shreespark.pos_api.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    List<PaymentTransaction> findAllBySaleIdAndTenantId(UUID saleId, UUID tenantId);
    List<PaymentTransaction> findAllByCustomerIdAndTenantId(UUID customerId, UUID tenantId);
}
