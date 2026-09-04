package com.shreespark.pos_api.permission.dto.response;

import java.util.Set;

public record RolePermissionsResponse(
        String role,
        Set<String> permissions
) {}
