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
    private String titularNombre;
    private String titularEmail;
    private String titularTelefono;
    private EstadoCondominio estado;
    private String planNombre;
    private LocalDateTime fechaRegistro;
}