package com.shreespark.pos_api.platform.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.gst.dto.request.CreateGstRateRequest;
import com.shreespark.pos_api.gst.dto.request.UpdateGstRateRequest;
import com.shreespark.pos_api.gst.dto.response.GstRateResponse;
import com.shreespark.pos_api.gst.service.GstRateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/platform/gst-rates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PlatformGstController {

    private final GstRateService gstRateService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GstRateResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(gstRateService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GstRateResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(gstRateService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GstRateResponse>> create(
            @Valid @RequestBody CreateGstRateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(gstRateService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GstRateResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGstRateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(gstRateService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        gstRateService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("GST rate deleted", null));
    }
}
