package com.urbanpark.parking.domain.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String rol;
    private UUID tenantId;
    private UUID userId;
    private String nombre;
}