package com.shreespark.pos_api.customer.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.customer.dto.request.CreateCustomerRequest;
import com.shreespark.pos_api.customer.dto.request.UpdateCustomerRequest;
import com.shreespark.pos_api.customer.dto.response.CustomerResponse;
import com.shreespark.pos_api.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMERS_EDIT')")
    public ResponseEntity<ApiResponse<CustomerResponse>> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Customer created", customerService.create(tenantId, request)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMERS_VIEW')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAll(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getAll(tenantId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMERS_VIEW')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getById(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getById(tenantId, id)));
    }

    @GetMapping("/lookup")
    @PreAuthorize("hasAuthority('CUSTOMERS_VIEW')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getByPhone(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestParam String phone) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getByPhone(tenantId, phone)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMERS_EDIT')")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Customer updated", customerService.update(tenantId, id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMERS_EDIT')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id) {
        customerService.delete(tenantId, id);
        return ResponseEntity.ok(ApiResponse.ok("Customer deleted", null));
    }
}
