package com.shreespark.pos_api.product.service.impl;

import com.shreespark.pos_api.brand.entity.Brand;
import com.shreespark.pos_api.brand.repository.BrandRepository;
import com.shreespark.pos_api.category.entity.Category;
import com.shreespark.pos_api.category.repository.CategoryRepository;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.common.service.FileStorageService;
import com.shreespark.pos_api.inventory.entity.StockLedger;
import com.shreespark.pos_api.inventory.repository.StockLedgerRepository;
import com.shreespark.pos_api.product.dto.request.CreateProductRequest;
import com.shreespark.pos_api.product.dto.request.UpdateProductRequest;
import com.shreespark.pos_api.product.dto.response.ProductResponse;
import com.shreespark.pos_api.product.entity.Product;
import com.shreespark.pos_api.product.mapper.ProductMapper;
import com.shreespark.pos_api.product.repository.ProductRepository;
import com.shreespark.pos_api.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final StockLedgerRepository stockLedgerRepository;
    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ProductResponse create(UUID tenantId, CreateProductRequest req) {
        if (req.sku() != null && productRepository.existsBySkuAndTenantId(req.sku(), tenantId)) {
            throw new RuntimeException("SKU already exists: " + req.sku());
        }

        Product product = Product.builder()
                .name(req.name())
                .description(req.description())
                .sku(req.sku())
                .retailPrice(req.retailPrice())
                .wholesalePrice(req.wholesalePrice())
                .dealerPrice(req.dealerPrice())
                .costPrice(req.costPrice())
                .lowStockThreshold(req.lowStockThreshold() != null ? req.lowStockThreshold() : 5)
                .moq(req.moq() != null ? req.moq() : 1)
                .category(resolveCategory(tenantId, req.categoryId()))
                .brand(resolveBrand(tenantId, req.brandId()))
                .build();

        product.setTenantId(tenantId);
        Product saved = productRepository.save(product);

        StockLedger ledger = StockLedger.builder().product(saved).currentStock(0).build();
        ledger.setTenantId(tenantId);
        stockLedgerRepository.save(ledger);

        return productMapper.toResponse(saved);
    }

    @Override
    public ProductResponse getById(UUID tenantId, UUID id) {
        return productMapper.toResponse(findOrThrow(tenantId, id));
    }

    @Override
    public List<ProductResponse> getAll(UUID tenantId) {
        return productRepository.findAllByTenantIdAndActiveTrue(tenantId)
                .stream().map(productMapper::toResponse).toList();
    }

    @Override
    public List<ProductResponse> getLowStock(UUID tenantId) {
        return stockLedgerRepository.findLowStockByTenantId(tenantId)
                .stream().map(sl -> productMapper.toResponse(sl.getProduct())).toList();
    }

    @Override
    public ProductResponse lookupByBarcode(UUID tenantId, String barcodeValue) {
        return productMapper.toResponse(
                productRepository.findByBarcodeValueAndTenantId(barcodeValue, tenantId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product", barcodeValue))
        );
    }

    @Override
    @Transactional
    public ProductResponse update(UUID tenantId, UUID id, UpdateProductRequest req) {
        Product product = findOrThrow(tenantId, id);

        if (req.name() != null) product.setName(req.name());
        if (req.description() != null) product.setDescription(req.description());
        if (req.sku() != null) product.setSku(req.sku());
        if (req.retailPrice() != null) product.setRetailPrice(req.retailPrice());
        if (req.wholesalePrice() != null) product.setWholesalePrice(req.wholesalePrice());
        if (req.dealerPrice() != null) product.setDealerPrice(req.dealerPrice());
        if (req.costPrice() != null) product.setCostPrice(req.costPrice());
        if (req.lowStockThreshold() != null) product.setLowStockThreshold(req.lowStockThreshold());
        if (req.moq() != null) product.setMoq(req.moq());
        if (req.categoryId() != null) product.setCategory(resolveCategory(tenantId, req.categoryId()));
        if (req.brandId() != null) product.setBrand(resolveBrand(tenantId, req.brandId()));

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse uploadImage(UUID tenantId, UUID id, MultipartFile file) {
        Product product = findOrThrow(tenantId, id);
        if (product.getImageUrl() != null) fileStorageService.delete(product.getImageUrl());
        product.setImageUrl(fileStorageService.store(file, "products"));
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(UUID tenantId, UUID id) {
        Product product = findOrThrow(tenantId, id);
        product.setActive(false);
        product.setDeletedAt(Instant.now());
        productRepository.save(product);
    }

    private Product findOrThrow(UUID tenantId, UUID id) {
        return productRepository.findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private Category resolveCategory(UUID tenantId, UUID categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findByIdAndTenantIdAndActiveTrue(categoryId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
    }

    private Brand resolveBrand(UUID tenantId, UUID brandId) {
        if (brandId == null) return null;
        return brandRepository.findByIdAndTenantIdAndActiveTrue(brandId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", brandId));
    }
}
