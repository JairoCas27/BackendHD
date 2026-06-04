package com.urbanpark.parking.domain.rules.dto;

import com.urbanpark.parking.shared.enums.TipoRegla;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReglaResponse {

    private Long id;
    private Long condominioId;
    private String condominioNombre;
    private TipoRegla tipo;
    private String nombre;
    private String descripcion;
    private String configuracion;
    private boolean activa;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}

