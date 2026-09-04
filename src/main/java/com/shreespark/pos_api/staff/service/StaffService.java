package com.shreespark.pos_api.staff.service;

import com.shreespark.pos_api.staff.dto.request.CreateStaffRequest;
import com.shreespark.pos_api.staff.dto.request.UpdateStaffRequest;
import com.shreespark.pos_api.staff.dto.response.StaffResponse;

import java.util.List;
import java.util.UUID;

public interface StaffService {
    StaffResponse create(UUID tenantId, CreateStaffRequest request);
    StaffResponse getById(UUID tenantId, UUID staffId);
    List<StaffResponse> getAllByTenant(UUID tenantId);
    StaffResponse update(UUID tenantId, UUID staffId, UpdateStaffRequest request);
    void deactivate(UUID tenantId, UUID staffId);
}
