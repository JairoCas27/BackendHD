package com.urbanpark.parking.domain.rules.dto;

import com.urbanpark.parking.shared.enums.TipoRegla;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class ReglaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo es obligatorio")
    private TipoRegla tipo;

    @NotNull(message = "La configuración es obligatoria")
    private Map<String, Object> configuracion;
}