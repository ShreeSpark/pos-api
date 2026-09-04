package com.shreespark.pos_api.subscription.service.impl;

import com.shreespark.pos_api.common.enums.SubscriptionPlan;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.subscription.dto.request.UpdatePlanConfigRequest;
import com.shreespark.pos_api.subscription.dto.response.SubscriptionPlanConfigResponse;
import com.shreespark.pos_api.subscription.entity.SubscriptionPlanConfig;
import com.shreespark.pos_api.subscription.mapper.SubscriptionPlanConfigMapper;
import com.shreespark.pos_api.subscription.repository.SubscriptionPlanConfigRepository;
import com.shreespark.pos_api.subscription.service.SubscriptionPlanConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanConfigServiceImpl implements SubscriptionPlanConfigService {

    private final SubscriptionPlanConfigRepository repository;
    private final SubscriptionPlanConfigMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanConfigResponse> getAll() {
        return repository.findAllByActiveTrue().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionPlanConfigResponse getByPlan(SubscriptionPlan plan) {
        return mapper.toResponse(repository.findByPlan(plan)
                .orElseThrow(() -> new ResourceNotFoundException("SubscriptionPlanConfig", plan.name())));
    }

    @Override
    @Transactional
    public SubscriptionPlanConfigResponse update(UUID id, UpdatePlanConfigRequest req) {
        SubscriptionPlanConfig config = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubscriptionPlanConfig", id));
        config.setDisplayName(req.displayName());
        config.setMonthlyPrice(req.monthlyPrice());
        config.setYearlyPrice(req.yearlyPrice());
        config.setMaxDevices(req.maxDevices());
        config.setMaxStaff(req.maxStaff());
        config.setMaxProducts(req.maxProducts());
        config.setFeatures(req.features());
        return mapper.toResponse(repository.save(config));
    }
}
