package com.urbanpark.parking.domain.users.vehiculo.dto;

import com.urbanpark.parking.shared.enums.TipoVehiculo;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class VehiculoResponse {
    private UUID id;
    private UUID usuarioId;
    private String placa;
    private String marca;
    private String modelo;
    private String color;
    private TipoVehiculo tipo;
    private boolean activo;
}