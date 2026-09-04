package com.shreespark.pos_api.gst.service;

import com.shreespark.pos_api.gst.dto.request.CreateGstRateRequest;
import com.shreespark.pos_api.gst.dto.request.UpdateGstRateRequest;
import com.shreespark.pos_api.gst.dto.response.GstRateResponse;

import java.util.List;
import java.util.UUID;

public interface GstRateService {
    GstRateResponse create(CreateGstRateRequest request);
    GstRateResponse getById(UUID id);
    List<GstRateResponse> getAll();
    GstRateResponse update(UUID id, UpdateGstRateRequest request);
    void delete(UUID id);
}
