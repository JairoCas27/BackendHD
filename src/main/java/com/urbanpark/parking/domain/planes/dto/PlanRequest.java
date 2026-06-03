package com.urbanpark.parking.domain.planes.dto;

import com.urbanpark.parking.shared.enums.EstadoPlan;
import com.urbanpark.parking.shared.enums.LimiteCondominios;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlanRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El límite de condominios es obligatorio")
    private LimiteCondominios limiteCondominios;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotBlank(message = "La moneda es obligatoria")
    private String moneda;

    @NotNull(message = "El estado es obligatorio")
    private EstadoPlan estado;
}