package com.shreespark.pos_api.barcode.service.impl;

import com.shreespark.pos_api.barcode.dto.request.GenerateBarcodeRequest;
import com.shreespark.pos_api.barcode.dto.response.BarcodeResponse;
import com.shreespark.pos_api.barcode.entity.Barcode;
import com.shreespark.pos_api.barcode.mapper.BarcodeMapper;
import com.shreespark.pos_api.barcode.repository.BarcodeRepository;
import com.shreespark.pos_api.barcode.service.BarcodeGeneratorService;
import com.shreespark.pos_api.barcode.service.BarcodeService;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.product.entity.Product;
import com.shreespark.pos_api.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BarcodeServiceImpl implements BarcodeService {

    private final BarcodeRepository barcodeRepository;
    private final ProductRepository productRepository;
    private final BarcodeGeneratorService barcodeGeneratorService;
    private final BarcodeMapper barcodeMapper;

    @Override
    @Transactional
    public BarcodeResponse generate(UUID tenantId, UUID productId, GenerateBarcodeRequest request) {
        Product product = productRepository.findByIdAndTenantIdAndActiveTrue(productId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        if (barcodeRepository.existsByValueAndProductTenantId(request.value(), tenantId)) {
            throw new RuntimeException("Barcode value already exists: " + request.value());
        }

        String imageBase64 = barcodeGeneratorService.generate(request.value(), request.format());

        Barcode barcode = Barcode.builder()
                .product(product)
                .value(request.value())
                .format(request.format())
                .imageBase64(imageBase64)
                .build();

        barcode.setTenantId(tenantId);
        return barcodeMapper.toResponse(barcodeRepository.save(barcode));
    }

    @Override
    public List<BarcodeResponse> getByProduct(UUID tenantId, UUID productId) {
        return barcodeRepository.findAllByProductIdAndProductTenantId(productId, tenantId)
                .stream().map(barcodeMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(UUID tenantId, UUID barcodeId) {
        if (!barcodeRepository.existsById(barcodeId)) {
            throw new ResourceNotFoundException("Barcode", barcodeId);
        }
        barcodeRepository.deleteByIdAndProductTenantId(barcodeId, tenantId);
    }

    @Override
    public byte[] downloadPng(UUID tenantId, UUID barcodeId) {
        Barcode barcode = barcodeRepository.findById(barcodeId)
                .filter(b -> b.getProduct().getTenantId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Barcode", barcodeId));

        return Base64.getDecoder().decode(barcode.getImageBase64());
    }

    @Override
    public byte[] downloadProductSheet(UUID tenantId, UUID productId) {
        List<Barcode> barcodes = barcodeRepository
                .findAllByProductIdAndProductTenantId(productId, tenantId);

        if (barcodes.isEmpty()) {
            throw new RuntimeException("No barcodes found for product: " + productId);
        }

        List<String> base64Images = barcodes.stream().map(Barcode::getImageBase64).toList();
        List<String> labels       = barcodes.stream().map(Barcode::getValue).toList();

        return barcodeGeneratorService.generateSheet(base64Images, labels);
    }
}
