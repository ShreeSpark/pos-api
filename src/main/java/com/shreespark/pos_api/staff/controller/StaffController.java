package com.shreespark.pos_api.staff.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.staff.dto.request.CreateStaffRequest;
import com.shreespark.pos_api.staff.dto.request.UpdateStaffRequest;
import com.shreespark.pos_api.staff.dto.response.StaffResponse;
import com.shreespark.pos_api.staff.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<StaffResponse>> create(
            @AuthenticationPrincipal String staffId,
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Staff created", staffService.create(tenantId, request)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getAll(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.getAllByTenant(tenantId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<StaffResponse>> getById(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.getById(tenantId, id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<StaffResponse>> update(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @RequestBody UpdateStaffRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Staff updated", staffService.update(tenantId, id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        staffService.deactivate(tenantId, id);
        return ResponseEntity.ok(ApiResponse.ok("Staff deactivated", null));
    }
}
