package com.urbanpark.parking.domain.users.visitante.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VisitanteRequest {

    @NotNull(message = "El propietarioId es obligatorio")
    private UUID propietarioId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String placaVehiculo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDateTime fechaFin;
}