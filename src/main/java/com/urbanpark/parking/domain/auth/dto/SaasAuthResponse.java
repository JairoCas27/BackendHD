package com.urbanpark.parking.domain.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class SaasAuthResponse {
    private String accessToken;
    private String refreshToken;
    private UUID userId;
    private String nombre;
    private String rol; // SUPERADMIN | ADMIN
}