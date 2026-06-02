package com.urbanpark.parking.domain.parking_management.dto;

import com.urbanpark.parking.shared.enums.EstadoEspacio;
import com.urbanpark.parking.shared.enums.TipoEspacio;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class EspacioResponse {
    private UUID id;
    private String codigo;
    private String zona;
    private TipoEspacio tipo;
    private EstadoEspacio estado;
    private UUID vehiculoActualId;
}