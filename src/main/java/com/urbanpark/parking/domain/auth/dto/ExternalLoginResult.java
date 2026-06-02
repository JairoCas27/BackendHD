package com.urbanpark.parking.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExternalLoginResult {
    private UsuarioExternoDTO usuario;
    private String accessToken;
    private String refreshToken;
}