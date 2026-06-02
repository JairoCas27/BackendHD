package com.urbanpark.parking.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityContextHelper {

    private final HttpServletRequest request;

    public UUID getCurrentUserId() {
        return UUID.fromString((String) request.getAttribute("userId"));
    }

    public UUID getCurrentTenantId() {
        return UUID.fromString((String) request.getAttribute("tenantId"));
    }

    public String getCurrentRol() {
        return (String) request.getAttribute("rol");
    }
}