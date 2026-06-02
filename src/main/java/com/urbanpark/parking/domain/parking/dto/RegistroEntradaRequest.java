package com.urbanpark.parking.domain.parking.dto;

import com.urbanpark.parking.shared.enums.MetodoAcceso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class RegistroEntradaRequest {

    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    @NotNull(message = "El método es obligatorio")
    private MetodoAcceso metodo;

    private UUID agenteId;
    private String rolUsuario;
}