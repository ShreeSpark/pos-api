package com.shreespark.pos_api.category.service;

import com.shreespark.pos_api.category.dto.request.CreateCategoryRequest;
import com.shreespark.pos_api.category.dto.request.UpdateCategoryRequest;
import com.shreespark.pos_api.category.dto.response.CategoryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse create(UUID tenantId, CreateCategoryRequest request);
    CategoryResponse getById(UUID tenantId, UUID id);
    List<CategoryResponse> getAll(UUID tenantId);
    CategoryResponse update(UUID tenantId, UUID id, UpdateCategoryRequest request);
    CategoryResponse uploadImage(UUID tenantId, UUID id, MultipartFile file);
    void delete(UUID tenantId, UUID id);
}
