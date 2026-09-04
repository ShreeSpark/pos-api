package com.shreespark.pos_api.product.mapper;

import com.shreespark.pos_api.category.entity.Category;
import com.shreespark.pos_api.gst.entity.GstRate;
import com.shreespark.pos_api.product.dto.response.ProductResponse;
import com.shreespark.pos_api.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getSku(),
                p.getImageUrl(),
                p.getRetailPrice(),
                p.getWholesalePrice(),
                p.getDealerPrice(),
                p.getCostPrice(),
                p.getLowStockThreshold(),
                p.getMoq(),
                toCategorySummary(p.getCategory()),
                p.getBrand() != null
                        ? new ProductResponse.BrandSummary(p.getBrand().getId(), p.getBrand().getName())
                        : null,
                p.getBarcodes().stream()
                        .map(b -> new ProductResponse.BarcodeSummary(b.getId(), b.getValue(), b.getFormat().name()))
                        .toList(),
                p.getCreatedAt()
        );
    }

    private ProductResponse.CategorySummary toCategorySummary(Category c) {
        if (c == null) return null;
        GstRate g = c.getGstRate();
        return new ProductResponse.CategorySummary(
                c.getId(),
                c.getName(),
                c.getHsnCode(),
                g != null ? g.getName() : null,
                g != null ? g.getRate() : null,
                g != null ? g.getCgstRate() : null,
                g != null ? g.getSgstRate() : null,
                g != null ? g.getIgstRate() : null
        );
    }
}
