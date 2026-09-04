package com.shreespark.pos_api.sales.repository;

import com.shreespark.pos_api.sales.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {
    Optional<Sale> findByIdAndTenantId(UUID id, UUID tenantId);
    Optional<Sale> findByInvoiceNumberAndTenantId(String invoiceNumber, UUID tenantId);
    List<Sale> findAllByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    List<Sale> findAllByTenantIdAndCustomerIdOrderByCreatedAtDesc(UUID tenantId, UUID customerId);

    @Query("SELECT s FROM Sale s WHERE s.tenantId = :tenantId " +
           "AND s.createdAt BETWEEN :from AND :to ORDER BY s.createdAt DESC")
    List<Sale> findByTenantIdAndDateRange(@Param("tenantId") UUID tenantId,
                                          @Param("from") Instant from,
                                          @Param("to") Instant to);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(s.invoiceNumber, 5) AS int)), 0) " +
           "FROM Sale s WHERE s.tenantId = :tenantId")
    Integer findMaxInvoiceSequence(@Param("tenantId") UUID tenantId);
}
