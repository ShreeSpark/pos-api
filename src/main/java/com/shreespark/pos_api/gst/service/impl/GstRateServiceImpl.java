package com.shreespark.pos_api.gst.service.impl;

import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.gst.dto.request.CreateGstRateRequest;
import com.shreespark.pos_api.gst.dto.request.UpdateGstRateRequest;
import com.shreespark.pos_api.gst.dto.response.GstRateResponse;
import com.shreespark.pos_api.gst.entity.GstRate;
import com.shreespark.pos_api.gst.mapper.GstRateMapper;
import com.shreespark.pos_api.gst.repository.GstRateRepository;
import com.shreespark.pos_api.gst.service.GstRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GstRateServiceImpl implements GstRateService {

    private final GstRateRepository gstRateRepository;
    private final GstRateMapper gstRateMapper;

    @Override
    @Transactional
    public GstRateResponse create(CreateGstRateRequest request) {
        if (gstRateRepository.existsByRate(request.rate())) {
            throw new RuntimeException("GST rate already exists: " + request.rate() + "%");
        }
        GstRate gstRate = buildGstRate(request.name(), request.rate(), request.description());
        return gstRateMapper.toResponse(gstRateRepository.save(gstRate));
    }

    @Override
    public GstRateResponse getById(UUID id) {
        return gstRateMapper.toResponse(findOrThrow(id));
    }

    @Override
    public List<GstRateResponse> getAll() {
        return gstRateRepository.findAllByActiveTrue()
                .stream().map(gstRateMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public GstRateResponse update(UUID id, UpdateGstRateRequest request) {
        GstRate gstRate = findOrThrow(id);

        if (request.name() != null) gstRate.setName(request.name());
        if (request.description() != null) gstRate.setDescription(request.description());

        if (request.rate() != null) {
            gstRate.setRate(request.rate());
            BigDecimal half = request.rate().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
            gstRate.setCgstRate(half);
            gstRate.setSgstRate(half);
            gstRate.setIgstRate(request.rate());
        }

        return gstRateMapper.toResponse(gstRateRepository.save(gstRate));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        GstRate gstRate = findOrThrow(id);
        gstRate.setActive(false);
        gstRate.setDeletedAt(Instant.now());
        gstRateRepository.save(gstRate);
    }

    private GstRate buildGstRate(String name, BigDecimal rate, String description) {
        BigDecimal half = rate.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        GstRate g = GstRate.builder()
                .name(name)
                .rate(rate)
                .cgstRate(half)
                .sgstRate(half)
                .igstRate(rate)
                .description(description)
                .build();
        return g;
    }

    private GstRate findOrThrow(UUID id) {
        return gstRateRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("GstRate", id));
    }
}
