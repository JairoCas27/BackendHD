package com.urbanpark.parking.domain.security_operations.dto;

import com.urbanpark.parking.shared.enums.NivelIncidente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class IncidenteRequest {

    // ID de la sesion activa del usuario que reporta (obtenida del login)
    @NotNull(message = "El sesionId es obligatorio")
    private UUID sesionId;

    private UUID accesoId;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotNull(message = "El nivel es obligatorio")
    private NivelIncidente nivel;

    private String placaInvolucrada;
}