package com.shreespark.pos_api.barcode.controller;

import com.shreespark.pos_api.barcode.dto.request.GenerateBarcodeRequest;
import com.shreespark.pos_api.barcode.dto.response.BarcodeResponse;
import com.shreespark.pos_api.barcode.service.BarcodeService;
import com.shreespark.pos_api.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products/{productId}/barcodes")
@RequiredArgsConstructor
public class BarcodeController {

    private final BarcodeService barcodeService;

    // Generate and save a new barcode for a product
    @PostMapping
    @PreAuthorize("hasAuthority('BARCODE_GENERATE')")
    public ResponseEntity<ApiResponse<BarcodeResponse>> generate(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID productId,
            @Valid @RequestBody GenerateBarcodeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Barcode generated", barcodeService.generate(tenantId, productId, request)));
    }

    // List all barcodes for a product (with base64 image for inline display)
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCTS_EDIT')")
    public ResponseEntity<ApiResponse<List<BarcodeResponse>>> getByProduct(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.ok(barcodeService.getByProduct(tenantId, productId)));
    }

    // Download a single barcode as PNG file (for label printing)
    @GetMapping(value = "/{barcodeId}/download", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasAuthority('BARCODE_GENERATE')")
    public ResponseEntity<byte[]> downloadPng(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID productId,
            @PathVariable UUID barcodeId) {
        byte[] png = barcodeService.downloadPng(tenantId, barcodeId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("barcode-" + barcodeId + ".png").build().toString())
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    // Download all barcodes of a product as a single printable sheet PNG
    @GetMapping(value = "/sheet", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasAuthority('BARCODE_GENERATE')")
    public ResponseEntity<byte[]> downloadSheet(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID productId) {
        byte[] sheet = barcodeService.downloadProductSheet(tenantId, productId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("barcodes-" + productId + ".png").build().toString())
                .contentType(MediaType.IMAGE_PNG)
                .body(sheet);
    }

    // Delete a barcode
    @DeleteMapping("/{barcodeId}")
    @PreAuthorize("hasAuthority('PRODUCTS_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID productId,
            @PathVariable UUID barcodeId) {
        barcodeService.delete(tenantId, barcodeId);
        return ResponseEntity.ok(ApiResponse.ok("Barcode deleted", null));
    }
}
