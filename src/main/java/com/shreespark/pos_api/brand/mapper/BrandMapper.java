package com.shreespark.pos_api.brand.mapper;

import com.shreespark.pos_api.brand.dto.response.BrandResponse;
import com.shreespark.pos_api.brand.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    public BrandResponse toResponse(Brand brand) {
        return new BrandResponse(
                brand.getId(),
                brand.getName(),
                brand.getDescription(),
                brand.getImageUrl(),
                brand.getCreatedAt()
        );
    }
}
