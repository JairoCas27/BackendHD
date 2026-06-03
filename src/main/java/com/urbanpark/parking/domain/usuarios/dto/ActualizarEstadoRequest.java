package com.urbanpark.parking.domain.usuarios.dto;

import com.urbanpark.parking.shared.enums.EstadoUsuarioSaas;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoUsuarioSaas estado;
}