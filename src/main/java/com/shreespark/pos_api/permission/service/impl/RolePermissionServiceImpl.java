package com.shreespark.pos_api.permission.service.impl;

import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.common.enums.Role;
import com.shreespark.pos_api.permission.dto.request.UpdateRolePermissionsRequest;
import com.shreespark.pos_api.permission.dto.response.RolePermissionsResponse;
import com.shreespark.pos_api.permission.entity.DefaultRolePermissions;
import com.shreespark.pos_api.permission.entity.RolePermission;
import com.shreespark.pos_api.permission.repository.RolePermissionRepository;
import com.shreespark.pos_api.permission.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void seedDefaultsForTenant(UUID tenantId) {
        for (Role role : Role.values()) {
            if (role == Role.SUPER_ADMIN) continue;
            Set<Permission> defaults = DefaultRolePermissions.forRole(role);
            for (Permission permission : defaults) {
                if (!rolePermissionRepository.existsByTenantIdAndRoleAndPermission(tenantId, role, permission)) {
                    rolePermissionRepository.save(RolePermission.builder()
                            .tenantId(tenantId)
                            .role(role)
                            .permission(permission)
                            .build());
                }
            }
        }
    }

    @Override
    public RolePermissionsResponse getByRole(UUID tenantId, Role role) {
        Set<Permission> permissions = rolePermissionRepository
                .findPermissionsByTenantIdAndRole(tenantId, role);
        return toResponse(role, permissions);
    }

    @Override
    public List<RolePermissionsResponse> getAll(UUID tenantId) {
        return Arrays.stream(Role.values())
                .filter(r -> r != Role.SUPER_ADMIN)
                .map(role -> getByRole(tenantId, role))
                .toList();
    }

    @Override
    @Transactional
    public RolePermissionsResponse setPermissions(UUID tenantId, UpdateRolePermissionsRequest request) {
        List<RolePermission> existing = rolePermissionRepository
                .findAllByTenantIdAndRole(tenantId, request.role());
        rolePermissionRepository.deleteAll(existing);

        List<RolePermission> updated = request.permissions().stream()
                .map(p -> RolePermission.builder()
                        .tenantId(tenantId)
                        .role(request.role())
                        .permission(p)
                        .build())
                .toList();

        rolePermissionRepository.saveAll(updated);
        return toResponse(request.role(), request.permissions());
    }

    @Override
    public Set<Permission> resolveEffectivePermissions(UUID tenantId, Role role, Set<Permission> overrides) {
        Set<Permission> roleDefaults = rolePermissionRepository
                .findPermissionsByTenantIdAndRole(tenantId, role);

        Set<Permission> effective = new HashSet<>(roleDefaults);
        if (overrides != null) effective.addAll(overrides);
        return effective;
    }

    private RolePermissionsResponse toResponse(Role role, Set<Permission> permissions) {
        return new RolePermissionsResponse(
                role.name(),
                permissions.stream().map(Enum::name).collect(Collectors.toSet())
        );
    }
}
