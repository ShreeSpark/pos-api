package com.shreespark.pos_api.permission.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.common.enums.Role;
import com.shreespark.pos_api.permission.dto.request.UpdateRolePermissionsRequest;
import com.shreespark.pos_api.permission.dto.response.RolePermissionsResponse;
import com.shreespark.pos_api.permission.service.RolePermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/permissions/roles")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<RolePermissionsResponse>>> getAll(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(rolePermissionService.getAll(tenantId)));
    }

    @GetMapping("/{role}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<RolePermissionsResponse>> getByRole(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable Role role) {
        return ResponseEntity.ok(ApiResponse.ok(rolePermissionService.getByRole(tenantId, role)));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RolePermissionsResponse>> setPermissions(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody UpdateRolePermissionsRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Permissions updated", rolePermissionService.setPermissions(tenantId, request)));
    }
}
