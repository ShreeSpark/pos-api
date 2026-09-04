package com.shreespark.pos_api.product.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.product.dto.request.CreateProductRequest;
import com.shreespark.pos_api.product.dto.request.UpdateProductRequest;
import com.shreespark.pos_api.product.dto.response.ProductResponse;
import com.shreespark.pos_api.product.service.ProductService;
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
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCTS_CREATE')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Product created", productService.create(tenantId, request)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getAll(tenantId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getById(tenantId, id)));
    }

    @GetMapping("/lookup/{barcodeValue}")
    @PreAuthorize("hasAuthority('BILLING_CREATE')")
    public ResponseEntity<ApiResponse<ProductResponse>> lookupByBarcode(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable String barcodeValue) {
        return ResponseEntity.ok(ApiResponse.ok(productService.lookupByBarcode(tenantId, barcodeValue)));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStock(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getLowStock(tenantId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Product updated", productService.update(tenantId, id, request)));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<ProductResponse>> uploadImage(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok("Image uploaded", productService.uploadImage(tenantId, id, file)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTS_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        productService.delete(tenantId, id);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted", null));
    }
}
