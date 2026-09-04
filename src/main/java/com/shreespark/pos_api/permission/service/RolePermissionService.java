package com.shreespark.pos_api.permission.service;

import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.common.enums.Role;
import com.shreespark.pos_api.permission.dto.request.UpdateRolePermissionsRequest;
import com.shreespark.pos_api.permission.dto.response.RolePermissionsResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RolePermissionService {
    void seedDefaultsForTenant(UUID tenantId);
    RolePermissionsResponse getByRole(UUID tenantId, Role role);
    List<RolePermissionsResponse> getAll(UUID tenantId);
    RolePermissionsResponse setPermissions(UUID tenantId, UpdateRolePermissionsRequest request);
    Set<Permission> resolveEffectivePermissions(UUID tenantId, Role role, Set<Permission> overrides);
}
