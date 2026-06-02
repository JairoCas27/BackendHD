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
    private String rol;
    private boolean activo;
    private LocalDateTime createdAt;
}