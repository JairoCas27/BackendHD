package com.urbanpark.parking.domain.saas.plan.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlanRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Min(value = 1, message = "El mínimo de espacios es 1")
    private int maxEspacios;

    @Min(value = 1, message = "El mínimo de usuarios es 1")
    private int maxUsuarios;
}