package com.urbanpark.parking.domain.planes.dto;

import com.urbanpark.parking.shared.enums.EstadoPlan;
import com.urbanpark.parking.shared.enums.LimiteCondominios;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PlanResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private LimiteCondominios limiteCondominios;
    private int maxCondominios;
    private BigDecimal precio;
    private String moneda;
    private EstadoPlan estado;
    private LocalDateTime creadoEn;
}