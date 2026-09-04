package com.shreespark.pos_api.tenant.service.impl;

import com.shreespark.pos_api.common.enums.Role;
import com.shreespark.pos_api.common.enums.SubscriptionPlan;
import com.shreespark.pos_api.common.enums.TenantStatus;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.staff.entity.Staff;
import com.shreespark.pos_api.staff.repository.StaffRepository;
import com.shreespark.pos_api.tenant.dto.request.CreateTenantRequest;
import com.shreespark.pos_api.tenant.dto.request.UpdateTenantRequest;
import com.shreespark.pos_api.tenant.dto.response.TenantResponse;
import com.shreespark.pos_api.tenant.entity.Tenant;
import com.shreespark.pos_api.tenant.mapper.TenantMapper;
import com.shreespark.pos_api.tenant.repository.TenantRepository;
import com.shreespark.pos_api.permission.service.RolePermissionService;
import com.shreespark.pos_api.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final RolePermissionService rolePermissionService;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TenantResponse create(CreateTenantRequest request) {
        if (tenantRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered: " + request.email());
        }
        if (tenantRepository.existsByBusinessName(request.businessName())) {
            throw new RuntimeException("Business name already taken: " + request.businessName());
        }

        Tenant tenant = Tenant.builder()
                .businessName(request.businessName())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .gstin(request.gstin())
                .status(TenantStatus.ACTIVE)
                .subscriptionPlan(request.subscriptionPlan())
                .subscriptionExpiry(request.subscriptionExpiry())
                .build();

        Tenant saved = tenantRepository.save(tenant);
        rolePermissionService.seedDefaultsForTenant(saved.getId());

        Staff admin = Staff.builder()
                .name(request.adminName())
                .email(request.email())
                .password(passwordEncoder.encode(request.adminPassword()))
                .role(Role.ADMIN)
                .build();
        admin.setTenantId(saved.getId());
        staffRepository.save(admin);

        return tenantMapper.toResponse(saved);
    }

    @Override
    public TenantResponse getById(UUID id) {
        return tenantMapper.toResponse(findActiveOrThrow(id));
    }

    @Override
    public List<TenantResponse> getAll() {
        return tenantRepository.findAll().stream()
                .filter(Tenant::isActive)
                .map(tenantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TenantResponse update(UUID id, UpdateTenantRequest request) {
        Tenant tenant = findActiveOrThrow(id);

        if (request.businessName() != null) tenant.setBusinessName(request.businessName());
        if (request.phone() != null) tenant.setPhone(request.phone());
        if (request.address() != null) tenant.setAddress(request.address());
        if (request.gstin() != null) tenant.setGstin(request.gstin());

        return tenantMapper.toResponse(tenantRepository.save(tenant));
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Tenant tenant = findActiveOrThrow(id);
        tenant.setActive(false);
        tenant.setDeletedAt(Instant.now());
        tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public TenantResponse changeStatus(UUID id, TenantStatus status) {
        Tenant tenant = findActiveOrThrow(id);
        tenant.setStatus(status);
        return tenantMapper.toResponse(tenantRepository.save(tenant));
    }

    @Override
    @Transactional
    public TenantResponse changePlan(UUID id, SubscriptionPlan plan, LocalDate newExpiry) {
        Tenant tenant = findActiveOrThrow(id);
        tenant.setSubscriptionPlan(plan);
        tenant.setSubscriptionExpiry(newExpiry);
        return tenantMapper.toResponse(tenantRepository.save(tenant));
    }

    private Tenant findActiveOrThrow(UUID id) {
        return tenantRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", id));
    }
}
