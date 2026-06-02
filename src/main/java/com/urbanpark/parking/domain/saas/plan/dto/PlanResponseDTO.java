package com.urbanpark.parking.domain.saas.plan.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PlanResponseDTO {
    private UUID id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int maxEspacios;
    private int maxUsuarios;
    private String estado;
    private LocalDateTime createdAt;
}