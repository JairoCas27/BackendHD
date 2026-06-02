package com.urbanpark.parking.domain.users.visitante.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VisitanteResponse {
    private UUID id;
    private UUID propietarioId;
    private String nombre;
    private String placaVehiculo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private boolean activo;
}