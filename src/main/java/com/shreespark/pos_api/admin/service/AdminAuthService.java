package com.shreespark.pos_api.admin.service;

import com.shreespark.pos_api.admin.dto.AdminLoginResponse;
import com.shreespark.pos_api.admin.entity.Admin;
import com.shreespark.pos_api.admin.repository.AdminRepository;
import com.shreespark.pos_api.auth.service.JwtService;
import com.shreespark.pos_api.auth.dto.request.LoginRequest;
import com.shreespark.pos_api.common.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminLoginResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByEmailAndActiveTrue(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), admin.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return new AdminLoginResponse(
                jwtService.generateAdminAccessToken(admin),
                jwtService.generateRefreshToken(admin.getId()),
                new AdminLoginResponse.AdminProfile(
                        admin.getId().toString(),
                        admin.getName(),
                        admin.getEmail()
                )
        );
    }
}
