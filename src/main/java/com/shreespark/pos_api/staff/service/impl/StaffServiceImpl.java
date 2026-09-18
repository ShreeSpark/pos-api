package com.shreespark.pos_api.staff.service.impl;

import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.staff.dto.request.CreateStaffRequest;
import com.shreespark.pos_api.staff.dto.request.UpdateStaffRequest;
import com.shreespark.pos_api.staff.dto.response.StaffResponse;
import com.shreespark.pos_api.staff.entity.Staff;
import com.shreespark.pos_api.staff.mapper.StaffMapper;
import com.shreespark.pos_api.staff.repository.StaffRepository;
import com.shreespark.pos_api.staff.service.StaffService;
import com.shreespark.pos_api.subscription.service.PlanLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;
    private final PasswordEncoder passwordEncoder;
    private final PlanLimitService planLimitService;

    @Override
    @Transactional
    public StaffResponse create(UUID tenantId, CreateStaffRequest request) {
        planLimitService.checkLimit(tenantId, PlanLimitService.LimitType.STAFF,
                staffRepository.countByTenantIdAndActiveTrue(tenantId));

        String code = request.staffCode();
        if (code == null || code.isBlank()) {
            long nextCount = staffRepository.countByTenantIdAndActiveTrue(tenantId) + 1001;
            code = String.valueOf(nextCount); // Numeric digits only e.g. 1001, 1002, 1003
        }

        String email = request.email();
        if (email == null || email.isBlank()) {
            email = code.toLowerCase() + "@staff.pos";
        }

        if (staffRepository.existsByEmailAndTenantId(email, tenantId)) {
            throw new RuntimeException("Staff with email or code already exists: " + email);
        }

        Staff staff = Staff.builder()
                .name(request.name())
                .staffCode(code)
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role(request.role())
                .permissions(request.permissions())
                .build();

        staff.setTenantId(tenantId);
        return staffMapper.toResponse(staffRepository.save(staff));
    }

    @Override
    public StaffResponse getById(UUID tenantId, UUID staffId) {
        return staffMapper.toResponse(findOrThrow(tenantId, staffId));
    }

    @Override
    public List<StaffResponse> getAllByTenant(UUID tenantId) {
        return staffRepository.findAllByTenantIdAndActiveTrue(tenantId)
                .stream().map(staffMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public StaffResponse update(UUID tenantId, UUID staffId, UpdateStaffRequest request) {
        Staff staff = findOrThrow(tenantId, staffId);

        if (request.name() != null) staff.setName(request.name());
        if (request.phone() != null) staff.setPhone(request.phone());
        if (request.role() != null) staff.setRole(request.role());
        if (request.permissions() != null) staff.setPermissions(request.permissions());

        return staffMapper.toResponse(staffRepository.save(staff));
    }

    @Override
    @Transactional
    public void deactivate(UUID tenantId, UUID staffId) {
        Staff staff = findOrThrow(tenantId, staffId);
        staff.setActive(false);
        staff.setDeletedAt(Instant.now());
        staffRepository.save(staff);
    }

    private Staff findOrThrow(UUID tenantId, UUID staffId) {
        return staffRepository.findByIdAndTenantIdAndActiveTrue(staffId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId));
    }
}
