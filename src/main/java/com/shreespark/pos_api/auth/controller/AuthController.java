package com.shreespark.pos_api.auth.controller;

import com.shreespark.pos_api.auth.dto.request.LoginRequest;
import com.shreespark.pos_api.auth.dto.response.LoginResponse;
import com.shreespark.pos_api.auth.service.AuthService;
import com.shreespark.pos_api.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenant/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }
}
