package com.shreespark.pos_api.product.service;

import com.shreespark.pos_api.product.dto.request.CreateProductRequest;
import com.shreespark.pos_api.product.dto.request.UpdateProductRequest;
import com.shreespark.pos_api.product.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse create(UUID tenantId, CreateProductRequest request);
    ProductResponse getById(UUID tenantId, UUID id);
    List<ProductResponse> getAll(UUID tenantId);
    List<ProductResponse> getLowStock(UUID tenantId);
    ProductResponse lookupByBarcode(UUID tenantId, String barcodeValue);
    ProductResponse update(UUID tenantId, UUID id, UpdateProductRequest request);
    ProductResponse uploadImage(UUID tenantId, UUID id, MultipartFile file);
    void delete(UUID tenantId, UUID id);
}
