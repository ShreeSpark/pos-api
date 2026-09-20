package com.shreespark.pos_api.auth;

import com.shreespark.pos_api.auth.service.JwtService;
import com.shreespark.pos_api.common.enums.DeviceStatus;
import com.shreespark.pos_api.device.repository.DeviceRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final DeviceRepository deviceRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String deviceCode = request.getHeader("X-Device-Code");
        if (deviceCode != null && !deviceCode.isBlank()) {
            var devOpt = deviceRepository.findByDeviceCode(deviceCode);
            if (devOpt.isPresent()) {
                var dev = devOpt.get();
                if (dev.getStatus() == DeviceStatus.SUSPENDED || dev.getStatus() == DeviceStatus.TERMINATED) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":false,\"message\":\"Device license is suspended or revoked by vendor. Access denied.\"}");
                    return;
                }
            }
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        if (!jwtService.isTokenValid(token)) {
            chain.doFilter(request, response);
            return;
        }

        Claims claims = jwtService.parseToken(token);
        List<String> permissions = jwtService.extractPermissions(token);
        String role = claims.get("role", String.class);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));

        var auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}
