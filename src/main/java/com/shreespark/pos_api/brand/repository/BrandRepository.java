package com.shreespark.pos_api.brand.repository;

import com.shreespark.pos_api.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
    List<Brand> findAllByTenantIdAndActiveTrue(UUID tenantId);
    Optional<Brand> findByIdAndTenantIdAndActiveTrue(UUID id, UUID tenantId);
    boolean existsByNameAndTenantId(String name, UUID tenantId);
}
