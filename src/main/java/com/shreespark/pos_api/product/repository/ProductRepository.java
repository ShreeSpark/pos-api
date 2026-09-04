package com.shreespark.pos_api.product.repository;

import com.shreespark.pos_api.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndTenantIdAndActiveTrue(UUID id, UUID tenantId);

    List<Product> findAllByTenantIdAndActiveTrue(UUID tenantId);

    boolean existsBySkuAndTenantId(String sku, UUID tenantId);

    @Query("SELECT p FROM Product p JOIN p.barcodes b " +
           "WHERE b.value = :value AND p.tenantId = :tenantId AND p.active = true")
    Optional<Product> findByBarcodeValueAndTenantId(@Param("value") String value,
                                                     @Param("tenantId") UUID tenantId);

    List<Product> findAllByTenantIdAndCategoryIdAndActiveTrue(UUID tenantId, UUID categoryId);

    List<Product> findAllByTenantIdAndBrandIdAndActiveTrue(UUID tenantId, UUID brandId);
}
