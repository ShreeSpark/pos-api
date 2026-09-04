package com.shreespark.pos_api.brand.service;

import com.shreespark.pos_api.brand.dto.request.CreateBrandRequest;
import com.shreespark.pos_api.brand.dto.request.UpdateBrandRequest;
import com.shreespark.pos_api.brand.dto.response.BrandResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BrandService {
    BrandResponse create(UUID tenantId, CreateBrandRequest request);
    BrandResponse getById(UUID tenantId, UUID id);
    List<BrandResponse> getAll(UUID tenantId);
    BrandResponse update(UUID tenantId, UUID id, UpdateBrandRequest request);
    BrandResponse uploadImage(UUID tenantId, UUID id, MultipartFile file);
    void delete(UUID tenantId, UUID id);
}
