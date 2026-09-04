package com.shreespark.pos_api.tenant.repository;

import com.shreespark.pos_api.common.enums.TenantStatus;
import com.shreespark.pos_api.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByIdAndActiveTrue(UUID id);
    boolean existsByEmail(String email);
    boolean existsByBusinessName(String businessName);
    long countByStatus(TenantStatus status);
    long countByActiveTrue();
    @Query("SELECT t FROM Tenant t WHERE t.subscriptionExpiry BETWEEN :from AND :to AND t.active = true")
    List<Tenant> findExpiringBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
