package com.shreespark.pos_api.barcode.repository;

import com.shreespark.pos_api.barcode.entity.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BarcodeRepository extends JpaRepository<Barcode, UUID> {
    List<Barcode> findAllByProductIdAndProductTenantId(UUID productId, UUID tenantId);
    Optional<Barcode> findByValueAndProductTenantId(String value, UUID tenantId);
    boolean existsByValueAndProductTenantId(String value, UUID tenantId);
    void deleteByIdAndProductTenantId(UUID id, UUID tenantId);
}
