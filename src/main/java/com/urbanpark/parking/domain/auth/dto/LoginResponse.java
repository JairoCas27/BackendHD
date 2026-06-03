package com.urbanpark.parking.domain.auth.dto;

import com.urbanpark.parking.shared.enums.EstadoUsuarioSaas;
import com.urbanpark.parking.shared.enums.RolSaas;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String token;
    private String refreshToken;
    private Long id;
    private String email;
    private String nombreCompleto;
    private RolSaas rol;
    private EstadoUsuarioSaas estado;
}