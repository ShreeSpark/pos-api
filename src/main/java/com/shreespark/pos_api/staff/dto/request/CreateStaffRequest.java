package com.shreespark.pos_api.staff.dto.request;

import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateStaffRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password,
        String phone,
        @NotNull Role role,
        Set<Permission> permissions
) {}
