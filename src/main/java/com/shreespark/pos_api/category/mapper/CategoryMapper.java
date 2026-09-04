package com.shreespark.pos_api.category.mapper;

import com.shreespark.pos_api.category.dto.response.CategoryResponse;
import com.shreespark.pos_api.category.entity.Category;
import com.shreespark.pos_api.gst.entity.GstRate;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category c) {
        return new CategoryResponse(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getImageUrl(),
                c.getHsnCode(),
                toGstSummary(c.getGstRate()),
                c.getCreatedAt()
        );
    }

    private CategoryResponse.GstRateSummary toGstSummary(GstRate g) {
        if (g == null) return null;
        return new CategoryResponse.GstRateSummary(
                g.getId(),
                g.getName(),
                g.getRate(),
                g.getCgstRate(),
                g.getSgstRate(),
                g.getIgstRate()
        );
    }
}
