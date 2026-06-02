package com.urbanpark.parking.domain.integration.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class SyncResultDTO {
    private UUID tenantId;
    private int usuariosCreados;
    private int usuariosActualizados;
    private int vehiculosCreados;
    private int vehiculosActualizados;
}