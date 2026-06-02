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
    private UUID agenteId;
    private UUID accesoId;
    private String descripcion;
    private NivelIncidente nivel;
    private EstadoIncidente estado;
    private String placaInvolucrada;
    private String resolucion;
    private LocalDateTime resueltoAt;
    private LocalDateTime createdAt;
}