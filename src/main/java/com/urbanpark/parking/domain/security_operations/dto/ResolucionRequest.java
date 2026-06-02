package com.urbanpark.parking.domain.security_operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolucionRequest {

    @NotBlank(message = "La resolución es obligatoria")
    private String resolucion;
}