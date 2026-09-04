package com.shreespark.pos_api.gst.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.gst.dto.request.CreateGstRateRequest;
import com.shreespark.pos_api.gst.dto.request.UpdateGstRateRequest;
import com.shreespark.pos_api.gst.dto.response.GstRateResponse;
import com.shreespark.pos_api.gst.service.GstRateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gst-rates")
@RequiredArgsConstructor
public class GstRateController {

    private final GstRateService gstRateService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<GstRateResponse>> create(
            @Valid @RequestBody CreateGstRateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("GST rate created", gstRateService.create(request)));
    }

    // All authenticated users can list GST rates (needed when creating categories)
    @GetMapping
    public ResponseEntity<ApiResponse<List<GstRateResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(gstRateService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GstRateResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(gstRateService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<GstRateResponse>> update(
            @PathVariable UUID id,
            @RequestBody UpdateGstRateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("GST rate updated", gstRateService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        gstRateService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("GST rate deleted", null));
    }
}
