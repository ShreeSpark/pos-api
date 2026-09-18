package com.shreespark.pos_api.auth.service.impl;

import com.shreespark.pos_api.auth.dto.request.LoginRequest;
import com.shreespark.pos_api.auth.dto.response.LoginResponse;
import com.shreespark.pos_api.auth.mapper.AuthMapper;
import com.shreespark.pos_api.auth.service.AuthService;
import com.shreespark.pos_api.auth.service.JwtService;
import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.common.exception.InvalidCredentialsException;
import com.shreespark.pos_api.permission.service.RolePermissionService;
import com.shreespark.pos_api.staff.entity.Staff;
import com.shreespark.pos_api.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final RolePermissionService rolePermissionService;

    @Override
    public LoginResponse login(LoginRequest request) {
        String identifier = request.email();
        List<Staff> candidates = staffRepository.findAllByStaffCodeAndActiveTrue(identifier);
        if (candidates.isEmpty()) {
            candidates = staffRepository.findAllByEmailAndActiveTrue(identifier);
        }
        if (candidates.isEmpty()) {
            candidates = staffRepository.findAllByPhoneAndActiveTrue(identifier);
        }

        Staff staff = candidates.stream()
                .filter(s -> passwordEncoder.matches(request.password(), s.getPassword()))
                .findFirst()
                .orElseThrow(InvalidCredentialsException::new);

        Set<Permission> effectivePermissions = rolePermissionService.resolveEffectivePermissions(
                staff.getTenantId(), staff.getRole(), staff.getPermissions());

        return new LoginResponse(
                jwtService.generateAccessToken(staff, effectivePermissions),
                jwtService.generateRefreshToken(staff.getId()),
                authMapper.toStaffProfile(staff, effectivePermissions)
        );
    }
}
