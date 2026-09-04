package com.shreespark.pos_api.staff.dto.request;

import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.common.enums.Role;

import java.util.Set;

public record UpdateStaffRequest(
        String name,
        String phone,
        Role role,
        Set<Permission> permissions
) {}
