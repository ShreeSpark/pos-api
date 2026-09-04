package com.shreespark.pos_api.auth.mapper;

import com.shreespark.pos_api.auth.dto.response.LoginResponse;
import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.staff.entity.Staff;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AuthMapper {

    public LoginResponse.StaffProfile toStaffProfile(Staff staff, Set<Permission> effectivePermissions) {
        return new LoginResponse.StaffProfile(
                staff.getId().toString(),
                staff.getName(),
                staff.getEmail(),
                staff.getRole().name(),
                effectivePermissions.stream().map(Enum::name).toList(),
                staff.getTenantId().toString()
        );
    }
}
