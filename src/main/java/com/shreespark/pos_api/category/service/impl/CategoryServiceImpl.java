package com.shreespark.pos_api.category.service.impl;

import com.shreespark.pos_api.category.dto.request.CreateCategoryRequest;
import com.shreespark.pos_api.category.dto.request.UpdateCategoryRequest;
import com.shreespark.pos_api.category.dto.response.CategoryResponse;
import com.shreespark.pos_api.category.entity.Category;
import com.shreespark.pos_api.category.mapper.CategoryMapper;
import com.shreespark.pos_api.category.repository.CategoryRepository;
import com.shreespark.pos_api.category.service.CategoryService;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.common.service.FileStorageService;
import com.shreespark.pos_api.gst.entity.GstRate;
import com.shreespark.pos_api.gst.repository.GstRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final FileStorageService fileStorageService;
    private final GstRateRepository gstRateRepository;

    @Override
    @Transactional
    public CategoryResponse create(UUID tenantId, CreateCategoryRequest request) {
        if (categoryRepository.existsByNameAndTenantId(request.name(), tenantId)) {
            throw new RuntimeException("Category already exists: " + request.name());
        }
        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .hsnCode(request.hsnCode())
                .gstRate(resolveGstRate(request.gstRateId()))
                .build();
        category.setTenantId(tenantId);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse getById(UUID tenantId, UUID id) {
        return categoryMapper.toResponse(findOrThrow(tenantId, id));
    }

    @Override
    public List<CategoryResponse> getAll(UUID tenantId) {
        return categoryRepository.findAllByTenantIdAndActiveTrue(tenantId)
                .stream().map(categoryMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID tenantId, UUID id, UpdateCategoryRequest request) {
        Category category = findOrThrow(tenantId, id);
        if (request.name() != null) category.setName(request.name());
        if (request.description() != null) category.setDescription(request.description());
        if (request.hsnCode() != null) category.setHsnCode(request.hsnCode());
        if (request.gstRateId() != null) category.setGstRate(resolveGstRate(request.gstRateId()));
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse uploadImage(UUID tenantId, UUID id, MultipartFile file) {
        Category category = findOrThrow(tenantId, id);
        if (category.getImageUrl() != null) fileStorageService.delete(category.getImageUrl());
        category.setImageUrl(fileStorageService.store(file, "categories"));
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(UUID tenantId, UUID id) {
        Category category = findOrThrow(tenantId, id);
        category.setActive(false);
        category.setDeletedAt(Instant.now());
        categoryRepository.save(category);
    }

    private Category findOrThrow(UUID tenantId, UUID id) {
        return categoryRepository.findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    private GstRate resolveGstRate(UUID gstRateId) {
        if (gstRateId == null) return null;
        return gstRateRepository.findByIdAndActiveTrue(gstRateId)
                .orElseThrow(() -> new ResourceNotFoundException("GstRate", gstRateId));
    }
}
