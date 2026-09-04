package com.shreespark.pos_api.staff.repository;

import com.shreespark.pos_api.staff.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffRepository extends JpaRepository<Staff, UUID> {
    Optional<Staff> findByEmailAndActiveTrue(String email);
    Optional<Staff> findByIdAndTenantIdAndActiveTrue(UUID id, UUID tenantId);
    List<Staff> findAllByTenantIdAndActiveTrue(UUID tenantId);
    boolean existsByEmailAndTenantId(String email, UUID tenantId);
}
