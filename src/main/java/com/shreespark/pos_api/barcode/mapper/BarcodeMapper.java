package com.shreespark.pos_api.barcode.mapper;

import com.shreespark.pos_api.barcode.dto.response.BarcodeResponse;
import com.shreespark.pos_api.barcode.entity.Barcode;
import org.springframework.stereotype.Component;

@Component
public class BarcodeMapper {

    public BarcodeResponse toResponse(Barcode barcode) {
        return new BarcodeResponse(
                barcode.getId(),
                barcode.getProduct().getId(),
                barcode.getValue(),
                barcode.getFormat().name(),
                barcode.getImageBase64()
        );
    }
}
