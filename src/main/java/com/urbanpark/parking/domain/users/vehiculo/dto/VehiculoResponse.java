package com.urbanpark.parking.domain.users.vehiculo.dto;

import com.urbanpark.parking.shared.enums.TipoVehiculo;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VehiculoResponse {
    // Local (BD propia)
    private UUID id;
    private UUID usuarioId;
    private boolean activo;

    // Externo (API condominio)
    private Long externalId;
    private Long propietarioId;
    private Long inquilinoId;
    private Long estacionamientoId;

    // Compartidos
    private String placa;
    private String marca;
    private String modelo;
    private String color;
    private TipoVehiculo tipo;
}