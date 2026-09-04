package com.shreespark.pos_api.permission.dto.request;

import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.common.enums.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateRolePermissionsRequest(
        @NotNull Role role,
        @NotEmpty Set<Permission> permissions
) {}
