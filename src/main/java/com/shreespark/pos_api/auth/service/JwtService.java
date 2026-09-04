package com.shreespark.pos_api.auth.service;

import com.shreespark.pos_api.admin.entity.Admin;
import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.staff.entity.Staff;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiry-ms}") long accessTokenExpiryMs,
            @Value("${app.jwt.refresh-token-expiry-ms}") long refreshTokenExpiryMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiryMs = accessTokenExpiryMs;
        this.refreshTokenExpiryMs = refreshTokenExpiryMs;
    }

    public String generateAdminAccessToken(Admin admin) {
        return Jwts.builder()
                .subject(admin.getId().toString())
                .claims(Map.of(
                        "adminId", admin.getId().toString(),
                        "role", "SUPER_ADMIN",
                        "permissions", List.of()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiryMs))
                .signWith(key)
                .compact();
    }

    public String generateAccessToken(Staff staff, Set<Permission> effectivePermissions) {
        return Jwts.builder()
                .subject(staff.getId().toString())
                .claims(Map.of(
                        "staffId", staff.getId().toString(),
                        "tenantId", staff.getTenantId().toString(),
                        "role", staff.getRole().name(),
                        "permissions", effectivePermissions.stream().map(Enum::name).toList()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiryMs))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UUID staffId) {
        return Jwts.builder()
                .subject(staffId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiryMs))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public UUID extractStaffId(String token) {
        return UUID.fromString(parseToken(token).getSubject());
    }

    public UUID extractTenantId(String token) {
        return UUID.fromString(parseToken(token).get("tenantId", String.class));
    }

    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        return parseToken(token).get("permissions", List.class);
    }
}
