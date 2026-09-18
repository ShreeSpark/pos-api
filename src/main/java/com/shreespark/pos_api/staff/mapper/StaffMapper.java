package com.shreespark.pos_api.staff.mapper;

import com.shreespark.pos_api.staff.dto.response.StaffResponse;
import com.shreespark.pos_api.staff.entity.Staff;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StaffMapper {

    public StaffResponse toResponse(Staff staff) {
        return new StaffResponse(
                staff.getId(),
                staff.getTenantId(),
                staff.getName(),
                staff.getStaffCode(),
                staff.getEmail(),
                staff.getPhone(),
                staff.getRole().name(),
                staff.getPermissions() != null ? staff.getPermissions().stream().map(Enum::name).collect(Collectors.toSet()) : Set.of(),
                staff.isActive(),
                staff.getCreatedAt()
        );
    }
}
