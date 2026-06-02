package com.urbanpark.parking.domain.parking.dto;

import com.urbanpark.parking.shared.enums.MetodoAcceso;
import com.urbanpark.parking.shared.enums.TipoEvento;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AccesoResponse {
    private UUID id;
    private UUID vehiculoId;
    private String placa;
    private UUID espacioId;
    private TipoEvento tipoEvento;
    private MetodoAcceso metodo;
    private UUID agenteId;
    private LocalDateTime timestampEntrada;
    private LocalDateTime timestampSalida;
    private Integer duracionMinutos;
    private boolean autorizado;
    private String motivoRechazo;
}