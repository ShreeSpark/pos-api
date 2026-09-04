package com.shreespark.pos_api.brand.service.impl;

import com.shreespark.pos_api.brand.dto.request.CreateBrandRequest;
import com.shreespark.pos_api.brand.dto.request.UpdateBrandRequest;
import com.shreespark.pos_api.brand.dto.response.BrandResponse;
import com.shreespark.pos_api.brand.entity.Brand;
import com.shreespark.pos_api.brand.mapper.BrandMapper;
import com.shreespark.pos_api.brand.repository.BrandRepository;
import com.shreespark.pos_api.brand.service.BrandService;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.common.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public BrandResponse create(UUID tenantId, CreateBrandRequest request) {
        if (brandRepository.existsByNameAndTenantId(request.name(), tenantId)) {
            throw new RuntimeException("Brand already exists: " + request.name());
        }
        Brand brand = Brand.builder()
                .name(request.name())
                .description(request.description())
                .build();
        brand.setTenantId(tenantId);
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override
    public BrandResponse getById(UUID tenantId, UUID id) {
        return brandMapper.toResponse(findOrThrow(tenantId, id));
    }

    @Override
    public List<BrandResponse> getAll(UUID tenantId) {
        return brandRepository.findAllByTenantIdAndActiveTrue(tenantId)
                .stream().map(brandMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public BrandResponse update(UUID tenantId, UUID id, UpdateBrandRequest request) {
        Brand brand = findOrThrow(tenantId, id);
        if (request.name() != null) brand.setName(request.name());
        if (request.description() != null) brand.setDescription(request.description());
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public BrandResponse uploadImage(UUID tenantId, UUID id, MultipartFile file) {
        Brand brand = findOrThrow(tenantId, id);
        if (brand.getImageUrl() != null) {
            fileStorageService.delete(brand.getImageUrl());
        }
        String path = fileStorageService.store(file, "brands");
        brand.setImageUrl(path);
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public void delete(UUID tenantId, UUID id) {
        Brand brand = findOrThrow(tenantId, id);
        brand.setActive(false);
        brand.setDeletedAt(Instant.now());
        brandRepository.save(brand);
    }

    private Brand findOrThrow(UUID tenantId, UUID id) {
        return brandRepository.findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
    }
}
