package com.urbanpark.parking.security;

import com.urbanpark.parking.domain.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);

            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String userId   = jwtService.extractUserId(token);
            String tenantId = jwtService.extractTenantId(token);
            String rol      = jwtService.extractRol(token);

            log.debug("JWT → userId={} | tenantId={} | rol={}", userId, tenantId, rol);

            // ─── Setea TenantContext directamente desde el filter ─────
            if (userId != null) {
                TenantContext.setUsuarioId(UUID.fromString(userId));
            }
            if (tenantId != null) {
                TenantContext.setTenantId(UUID.fromString(tenantId));
            }

            // Setea en request por compatibilidad con TenantFilter
            request.setAttribute("userId", userId);
            request.setAttribute("tenantId", tenantId);
            request.setAttribute("rol", rol);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } finally {
            // Limpia el ThreadLocal al final de cada request
            TenantContext.clear();
        }
    }
}