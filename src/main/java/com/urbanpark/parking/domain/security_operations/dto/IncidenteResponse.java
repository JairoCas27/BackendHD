package com.urbanpark.parking.domain.security_operations.dto;

import com.urbanpark.parking.shared.enums.EstadoIncidente;
import com.urbanpark.parking.shared.enums.NivelIncidente;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class IncidenteResponse {
    private UUID id;
    private UUID tenantId;

    // Datos de quien reportó
    private UUID sesionId;
    private String reportadoPor;   // nombre del usuario de la sesion
    private String rolReportador;  // rol del usuario de la sesion

    private UUID accesoId;
    private String descripcion;
    private NivelIncidente nivel;
    private EstadoIncidente estado;
    private String placaInvolucrada;
    private String resolucion;
    private LocalDateTime resueltoAt;
    private LocalDateTime createdAt;
}