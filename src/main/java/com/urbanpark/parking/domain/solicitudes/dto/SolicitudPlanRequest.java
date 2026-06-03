package com.urbanpark.parking.domain.solicitudes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudPlanRequest {

    @NotNull(message = "El ID del plan es obligatorio")
    private Long planId;
}