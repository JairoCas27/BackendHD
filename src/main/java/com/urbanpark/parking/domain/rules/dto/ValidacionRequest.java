package com.urbanpark.parking.domain.rules.dto;

import com.urbanpark.parking.shared.enums.TipoVehiculo;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ValidacionRequest {
    private UUID tenantId;
    private UUID vehiculoId;
    private String placa;
    private TipoVehiculo tipoVehiculo;
    private String rolUsuario;
    private boolean visitanteAutorizado;
    private int vehiculosActivosEnParking;
}