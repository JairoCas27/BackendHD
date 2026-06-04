package com.urbanpark.parking.domain.rules.dto;

import com.urbanpark.parking.shared.enums.TipoRegla;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReglaRequest {

    @NotNull(message = "El tipo de regla es obligatorio")
    private TipoRegla tipo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotBlank(message = "La configuración es obligatoria")
    private String configuracion;

    private Boolean activa;
}
