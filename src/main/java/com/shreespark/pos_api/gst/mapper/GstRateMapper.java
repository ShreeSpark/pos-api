package com.shreespark.pos_api.gst.mapper;

import com.shreespark.pos_api.gst.dto.response.GstRateResponse;
import com.shreespark.pos_api.gst.entity.GstRate;
import org.springframework.stereotype.Component;

@Component
public class GstRateMapper {

    public GstRateResponse toResponse(GstRate g) {
        return new GstRateResponse(
                g.getId(),
                g.getName(),
                g.getRate(),
                g.getCgstRate(),
                g.getSgstRate(),
                g.getIgstRate(),
                g.getDescription()
        );
    }
}
