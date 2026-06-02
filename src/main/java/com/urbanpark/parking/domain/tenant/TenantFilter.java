package com.urbanpark.parking.domain.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(2)
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String tenantId  = (String) request.getAttribute("tenantId");
            String usuarioId = (String) request.getAttribute("userId");

            if (tenantId != null) {
                TenantContext.setTenantId(UUID.fromString(tenantId));
            }

            if (usuarioId != null) {
                TenantContext.setUsuarioId(UUID.fromString(usuarioId));
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear(); // limpia tenant + usuario
        }
    }
}