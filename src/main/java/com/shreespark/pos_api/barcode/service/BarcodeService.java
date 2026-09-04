package com.shreespark.pos_api.barcode.service;

import com.shreespark.pos_api.barcode.dto.request.GenerateBarcodeRequest;
import com.shreespark.pos_api.barcode.dto.response.BarcodeResponse;

import java.util.List;
import java.util.UUID;

public interface BarcodeService {
    BarcodeResponse generate(UUID tenantId, UUID productId, GenerateBarcodeRequest request);
    List<BarcodeResponse> getByProduct(UUID tenantId, UUID productId);
    void delete(UUID tenantId, UUID barcodeId);

    // returns raw PNG bytes for direct download / label printing
    byte[] downloadPng(UUID tenantId, UUID barcodeId);

    // returns a single PNG sheet with all barcodes of a product (for bulk label printing)
    byte[] downloadProductSheet(UUID tenantId, UUID productId);
}
