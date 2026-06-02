package com.urbanpark.parking.domain.tenant.dto;

import com.urbanpark.parking.shared.enums.EstadoCondominio;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CondominioResponse {
    private UUID id;
    private String nombre;
    private String apiBaseUrl;

    // Titular
    private String titularNombre;
    private String titularDni;
    private String titularEmail;
    private String titularTelefono;

    // Plan
    private UUID planId;
    private String planNombre;

    // Estado
    private EstadoCondominio estado;

    // Usuario CLIENTE generado automaticamente
    private UUID clienteUserId;

    private LocalDateTime fechaRegistro;
}