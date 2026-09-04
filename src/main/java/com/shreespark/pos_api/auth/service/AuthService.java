package com.shreespark.pos_api.auth.service;

import com.shreespark.pos_api.auth.dto.request.LoginRequest;
import com.shreespark.pos_api.auth.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
