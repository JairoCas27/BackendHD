package com.urbanpark.parking.domain.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class AuthResponse {
    private UUID condominioId;
    private String condominioNombre;

    private Long externalUserId;
    private String nombre;
    private String email;
    private String rol;

    private String accessToken;
    private String refreshToken;
}