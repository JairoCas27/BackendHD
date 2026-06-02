package com.urbanpark.parking.domain.security_operations.dto;

import com.urbanpark.parking.shared.enums.NivelIncidente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class IncidenteRequest {

    @NotNull(message = "El agenteId es obligatorio")
    private UUID agenteId;

    private UUID accesoId;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El nivel es obligatorio")
    private NivelIncidente nivel;

    private String placaInvolucrada;
}