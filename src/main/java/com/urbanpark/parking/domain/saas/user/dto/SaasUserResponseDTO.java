package com.urbanpark.parking.domain.saas.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SaasUserResponseDTO {
    private UUID id;
    private String email;
    private String nombre;
    private String dni;
    private String telefono;
    private String cargo;
    private String rol;
    private boolean activo;
    private boolean esBase;
    private LocalDateTime createdAt;
}