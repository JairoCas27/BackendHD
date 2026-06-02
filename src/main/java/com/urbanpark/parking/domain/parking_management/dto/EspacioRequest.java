package com.urbanpark.parking.domain.parking_management.dto;

import com.urbanpark.parking.shared.enums.TipoEspacio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EspacioRequest {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "La zona es obligatoria")
    private String zona;

    @NotNull(message = "El tipo es obligatorio")
    private TipoEspacio tipo;
}