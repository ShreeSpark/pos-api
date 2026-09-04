package com.shreespark.pos_api.category.controller;

import com.shreespark.pos_api.category.dto.request.CreateCategoryRequest;
import com.shreespark.pos_api.category.dto.request.UpdateCategoryRequest;
import com.shreespark.pos_api.category.dto.response.CategoryResponse;
import com.shreespark.pos_api.category.service.CategoryService;
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
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCTS_CREATE')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Category created", categoryService.create(tenantId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getAll(tenantId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getById(tenantId, id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Category updated", categoryService.update(tenantId, id, request)));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<CategoryResponse>> uploadImage(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok("Image uploaded", categoryService.uploadImage(tenantId, id, file)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTS_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        categoryService.delete(tenantId, id);
        return ResponseEntity.ok(ApiResponse.ok("Category deleted", null));
    }
}
