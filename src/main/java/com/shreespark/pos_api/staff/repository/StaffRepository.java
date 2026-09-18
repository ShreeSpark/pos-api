package com.shreespark.pos_api.staff.repository;

import com.shreespark.pos_api.staff.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffRepository extends JpaRepository<Staff, UUID> {
    List<Staff> findAllByStaffCodeAndActiveTrue(String staffCode);
    List<Staff> findAllByEmailAndActiveTrue(String email);
    List<Staff> findAllByPhoneAndActiveTrue(String phone);
    Optional<Staff> findByStaffCodeAndActiveTrue(String staffCode);
    Optional<Staff> findByEmailAndActiveTrue(String email);
    Optional<Staff> findByPhoneAndActiveTrue(String phone);
    Optional<Staff> findByIdAndTenantIdAndActiveTrue(UUID id, UUID tenantId);
    List<Staff> findAllByTenantIdAndActiveTrue(UUID tenantId);
    boolean existsByEmailAndTenantId(String email, UUID tenantId);
    long countByTenantIdAndActiveTrue(UUID tenantId);
}
