package com.shreespark.pos_api.khata.repository;

import com.shreespark.pos_api.khata.entity.KhataEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface KhataEntryRepository extends JpaRepository<KhataEntry, UUID> {

    List<KhataEntry> findAllByCustomerIdAndCustomerTenantIdOrderByCreatedAtDesc(
            UUID customerId, UUID tenantId);

    @Query("SELECT ke FROM KhataEntry ke WHERE ke.customer.tenantId = :tenantId " +
           "AND ke.customer.outstandingBalance > 0 ORDER BY ke.customer.outstandingBalance DESC")
    List<KhataEntry> findOverdueByTenantId(@Param("tenantId") UUID tenantId);
}
