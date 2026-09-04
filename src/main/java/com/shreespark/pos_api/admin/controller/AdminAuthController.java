package com.shreespark.pos_api.admin.controller;

import com.shreespark.pos_api.admin.dto.AdminLoginResponse;
import com.shreespark.pos_api.admin.service.AdminAuthService;
import com.shreespark.pos_api.auth.dto.request.LoginRequest;
import com.shreespark.pos_api.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/platform/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AdminLoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(adminAuthService.login(request)));
    }
}
