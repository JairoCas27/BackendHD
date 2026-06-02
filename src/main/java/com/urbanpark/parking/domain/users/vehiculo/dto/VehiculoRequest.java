package com.urbanpark.parking.domain.users.vehiculo.dto;

import com.urbanpark.parking.shared.enums.TipoVehiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class VehiculoRequest {

    @NotNull(message = "El usuarioId es obligatorio")
    private UUID usuarioId;

    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    private String marca;
    private String modelo;
    private String color;

    @NotNull(message = "El tipo es obligatorio")
    private TipoVehiculo tipo;
}