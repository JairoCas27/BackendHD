package com.urbanpark.parking.modules.access.dto.request;

import com.urbanpark.parking.modules.access.domain.enums.AccessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterAccessRequest(
    @NotBlank(message = "La placa es obligatoria")
    String plate,

    @NotNull(message = "El tipo de acceso es obligatorio (ENTRADA/SALIDA)")
    AccessType accessType,

    String notes
) {}