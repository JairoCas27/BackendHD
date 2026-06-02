package com.urbanpark.parking.domain.parking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class RegistroSalidaRequest {

    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    private UUID agenteId;
}