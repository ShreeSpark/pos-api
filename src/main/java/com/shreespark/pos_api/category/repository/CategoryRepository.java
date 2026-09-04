package com.shreespark.pos_api.category.repository;

import com.shreespark.pos_api.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByTenantIdAndActiveTrue(UUID tenantId);
    Optional<Category> findByIdAndTenantIdAndActiveTrue(UUID id, UUID tenantId);
    boolean existsByNameAndTenantId(String name, UUID tenantId);
}
