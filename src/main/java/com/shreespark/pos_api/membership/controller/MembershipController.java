package com.shreespark.pos_api.membership.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.membership.dto.request.AssignMembershipRequest;
import com.shreespark.pos_api.membership.dto.request.CreateMembershipRequest;
import com.shreespark.pos_api.membership.dto.request.UpdateMembershipRequest;
import com.shreespark.pos_api.membership.dto.response.MembershipResponse;
import com.shreespark.pos_api.membership.dto.response.MembershipSubscriptionResponse;
import com.shreespark.pos_api.membership.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    // --- Membership tier management ---

    @PostMapping("/memberships")
    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    public ResponseEntity<ApiResponse<MembershipResponse>> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody CreateMembershipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Membership created", membershipService.create(tenantId, request)));
    }

    @GetMapping("/memberships")
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> getAll(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(membershipService.getAll(tenantId)));
    }

    @GetMapping("/memberships/{id}")
    public ResponseEntity<ApiResponse<MembershipResponse>> getById(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(membershipService.getById(tenantId, id)));
    }

    @PutMapping("/memberships/{id}")
    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    public ResponseEntity<ApiResponse<MembershipResponse>> update(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @RequestBody UpdateMembershipRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Membership updated", membershipService.update(tenantId, id, request)));
    }

    @DeleteMapping("/memberships/{id}")
    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        membershipService.delete(tenantId, id);
        return ResponseEntity.ok(ApiResponse.ok("Membership deleted", null));
    }

    // --- Customer membership assignment ---

    @PostMapping("/customers/{customerId}/membership")
    @PreAuthorize("hasAuthority('CUSTOMERS_EDIT')")
    public ResponseEntity<ApiResponse<MembershipSubscriptionResponse>> assign(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID customerId,
            @Valid @RequestBody AssignMembershipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Membership assigned",
                        membershipService.assign(tenantId, customerId, request)));
    }

    @GetMapping("/customers/{customerId}/membership")
    @PreAuthorize("hasAuthority('CUSTOMERS_VIEW')")
    public ResponseEntity<ApiResponse<MembershipSubscriptionResponse>> getActive(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.ok(
                membershipService.getActiveSubscription(tenantId, customerId).orElse(null)));
    }

    @GetMapping("/customers/{customerId}/memberships")
    @PreAuthorize("hasAuthority('CUSTOMERS_VIEW')")
    public ResponseEntity<ApiResponse<List<MembershipSubscriptionResponse>>> getHistory(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.ok(
                membershipService.getCustomerSubscriptions(tenantId, customerId)));
    }
}
