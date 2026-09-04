package com.shreespark.pos_api.brand.controller;

import com.shreespark.pos_api.brand.dto.request.CreateBrandRequest;
import com.shreespark.pos_api.brand.dto.request.UpdateBrandRequest;
import com.shreespark.pos_api.brand.dto.response.BrandResponse;
import com.shreespark.pos_api.brand.service.BrandService;
import com.shreespark.pos_api.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCTS_CREATE')")
    public ResponseEntity<ApiResponse<BrandResponse>> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody CreateBrandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Brand created", brandService.create(tenantId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAll(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(brandService.getAll(tenantId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getById(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(brandService.getById(tenantId, id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<BrandResponse>> update(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @RequestBody UpdateBrandRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Brand updated", brandService.update(tenantId, id, request)));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<BrandResponse>> uploadImage(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok("Image uploaded", brandService.uploadImage(tenantId, id, file)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTS_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        brandService.delete(tenantId, id);
        return ResponseEntity.ok(ApiResponse.ok("Brand deleted", null));
    }
}
