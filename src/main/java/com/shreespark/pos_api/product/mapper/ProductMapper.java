package com.shreespark.pos_api.product.mapper;

import com.shreespark.pos_api.category.entity.Category;
import com.shreespark.pos_api.gst.entity.GstRate;
import com.shreespark.pos_api.product.dto.response.ProductResponse;
import com.shreespark.pos_api.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product p) {
        GstRate effGst = p.getGstRate() != null ? p.getGstRate() : (p.getCategory() != null ? p.getCategory().getGstRate() : null);
        String effHsn = (effGst != null && effGst.getHsnCode() != null && !effGst.getHsnCode().isBlank())
                ? effGst.getHsnCode()
                : (p.getHsnCode() != null ? p.getHsnCode() : (p.getCategory() != null ? p.getCategory().getHsnCode() : null));

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
                effHsn,
                effGst != null ? effGst.getId() : null,
                effGst != null ? effGst.getName() : null,
                effGst != null ? effGst.getRate() : null,
                p.getCreatedAt()
        );
    }

    private ProductResponse.CategorySummary toCategorySummary(Category c) {
        if (c == null) return null;
        GstRate g = c.getGstRate();
        String catHsn = (g != null && g.getHsnCode() != null && !g.getHsnCode().isBlank())
                ? g.getHsnCode()
                : c.getHsnCode();
        return new ProductResponse.CategorySummary(
                c.getId(),
                c.getName(),
                catHsn,
                g != null ? g.getName() : null,
                g != null ? g.getRate() : null,
                g != null ? g.getCgstRate() : null,
                g != null ? g.getSgstRate() : null,
                g != null ? g.getIgstRate() : null
        );
    }
}
